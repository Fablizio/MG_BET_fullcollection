package com.betting.service;

import com.betting.entity.AiElaboration;
import com.betting.entity.Odd;
import com.betting.entity.Token;
import com.betting.entity.User;
import com.betting.enumeration.ElaborationStatus;
import com.betting.enumeration.TokenStatus;
import com.betting.repository.AiElaborationRepository;
import com.betting.repository.OddRepository;
import com.betting.repository.TokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class TokenService {

    @Autowired
    private TokenRepository tokenRepository;

    @Autowired
    private AiElaborationRepository aiElaborationRepository;

    public void addToken(User user) {
        tokenRepository.save(
                Token.builder()
                        .status(TokenStatus.UNSPENT)
                        .user(user)
                        .build()
        );
    }

    public long countAvailableTokens(User user) {
        return tokenRepository.countByUserAndStatus(user, TokenStatus.UNSPENT);
    }

    @Autowired
    private OddRepository oddRepository;


    @Transactional
    public void createElaboration(User user, List<Long> matchIds) {

        List<Odd> matches = oddRepository.findByIdIn(matchIds);
        if (matches.size() != matchIds.size()) {
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Una o più partite non esistono");
        }

        int tokensNeeded = matchIds.size();

        long availableToken = tokenRepository.countByUserAndStatus(user, TokenStatus.UNSPENT);

        if (availableToken < tokensNeeded) {
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Token insufficienti");
        }

        AiElaboration elaboration = AiElaboration.builder()
                .user(user)
                .matches(matches)
                .status(ElaborationStatus.PROCESSING)
                .createdAt(LocalDateTime.now())
                .build();

        aiElaborationRepository.save(elaboration);

        List<Token> tokenAvailable = tokenRepository.findByUserAndStatus(user, TokenStatus.UNSPENT);

        for (int i = 0; i < tokensNeeded; i++) {
            Token token = tokenAvailable.get(i);
            token.setElaboration(elaboration);
            token.setStatus(TokenStatus.SPENT);
            tokenRepository.save(token);
        }


    }


}
