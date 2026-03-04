package com.betting.service.impl;

import com.betting.dto.BroadcastMessageDTO;
import com.betting.entity.CodeFriend;
import com.betting.entity.User;
import com.betting.http.HttpClient;
import com.betting.repository.FriendCodeRepository;
import com.betting.repository.UserRepository;
import com.betting.service.IUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpStatusCodeException;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;

import static com.betting.security.JwtAuthenticationFilter.CODE;

@Slf4j
@Service
public class UserService implements IUserService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    private HttpClient httpClient;

    @Autowired
    private FriendCodeRepository friendCodeRepository;

    @Override
    public User findByCode(String code) {
        return userRepository.findByCode(code).orElse(null);
    }


    @Transactional
    public void applyFriendCode(String friendCode, HttpServletRequest request) {

        String code = request.getHeader(CODE);
        User user = findByCode(code);

        if (user.getPayment() != null && user.getPayment() <= 0) {
            throw new HttpClientErrorException(HttpStatus.UNPROCESSABLE_ENTITY,
                    "È necessario attivare un abbonamento prima di poter utilizzare il tuo codice amico");
        }

        if (user.isFriendCodeActive()) {
            throw new HttpClientErrorException(HttpStatus.UNPROCESSABLE_ENTITY, "Hai già utilizzato il codice amico!");
        }

        User owner = userRepository.findByFriendCode(friendCode).orElseThrow(
                () -> new HttpClientErrorException(HttpStatus.NOT_FOUND, "Il codice non è valido")
        );

        if (user.getFriendCode().equals(friendCode)) {
            throw new HttpClientErrorException(HttpStatus.UNPROCESSABLE_ENTITY, "Non puoi utilizzare il tuo codice amico!");
        }

        // BONUS OWNER (+2 settimane)
        LocalDate expirationOwner = getExpirationDate(owner);
        owner.setExpiration(expirationOwner.plusWeeks(2));
        userRepository.save(owner);

        String textOwner = "Ciao!\n" +
                "Hai appena ricevuto 2 settimane extra di abbonamento grazie all'utilizzo del tuo codice amico! 🎉\n\n" +
                "Grazie per condividere MGBet e per essere parte attiva della community.\n" +
                "Continua così — ogni volta che un tuo amico utilizza il tuo codice, ottieni altri giorni bonus! 💪🔥\n";

        httpClient.sendMessage(new BroadcastMessageDTO(textOwner, Collections.singletonList(owner.getTelegramSession())));

        friendCodeRepository.save(CodeFriend.builder()
                .friend(user)
                .user(owner)
                .build());

        // BONUS USER (+5 giorni)
        user.setFriendCodeActive(true);
        LocalDate expirationUser = getExpirationDate(user);
        user.setExpiration(expirationUser.plusDays(5));

        String textUser = "Ciao \n" +
                "Il tuo abbonamento è stato esteso di 5 giorni grazie al codice amico! \n" +
                "Ricorda che, condividendo il tuo codice (lo trovi nella sezione Account dell’app), puoi ottenere 2 settimane extra per ogni amico pagante che si unisce.";

        httpClient.sendMessage(new BroadcastMessageDTO(textUser, Collections.singletonList(user.getTelegramSession())));
        userRepository.save(user);
    }


    private LocalDate getExpirationDate(User user) {
        LocalDate expiration = user.getExpiration();
        LocalDate now = LocalDate.now();
        if (expiration.isAfter(LocalDate.now())) {
            now = expiration;
        }
        return now;
    }

    public User updateDiscordUsername(String discordUsername, String code) {
        User user = findByCode(code);
        user.setDiscordUsername(discordUsername);
        return userRepository.save(user);
    }
}
