package it.smibet.mappers;

import it.smibet.domain.Bet;
import it.smibet.dto.DailyBetDTO;
import it.smibet.enumeration.BetType;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

public class BetMapper {


    public static Bet fromDTO(DailyBetDTO dailyBetDTO, BetType dailyBetType) {
        return Bet.builder()
                .date(LocalDate.now())
                .betType(dailyBetType)
                .odds(OddMapper.fromDTO(dailyBetDTO.getOdds()))
                .build();
    }

    public static DailyBetDTO fromEntity(Bet bet) {
        return DailyBetDTO.builder()
                .date(bet.getDate().toString())
                .odds(OddMapper.fromEntity(bet.getOdds()))
                .build();
    }

    public static List<DailyBetDTO> fromEntity(List<Bet> bet) {
        return
                bet.stream().map(BetMapper::fromEntity).collect(Collectors.toList());
    }

}
