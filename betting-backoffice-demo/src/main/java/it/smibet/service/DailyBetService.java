package it.smibet.service;

import it.smibet.domain.Bet;
import it.smibet.dto.DailyBetDTO;
import it.smibet.enumeration.BetType;
import it.smibet.mappers.BetMapper;
import it.smibet.repository.BetRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DailyBetService {


    @Autowired
    private BetRepository betRepository;

    @Transactional
    public void save(DailyBetDTO dailyBetDTO, BetType dailyBetType) {

        betRepository.save(
                BetMapper.fromDTO(dailyBetDTO, dailyBetType)
        );

    }

    public List<DailyBetDTO> getAll(BetType betType) {
        return betRepository.findByBetType(betType)
                .stream()
                .sorted(Comparator.comparing(Bet::getDate).reversed())
                .map(BetMapper::fromEntity)
                .collect(Collectors.toList());
    }
}
