package it.smibet.mappers;

import it.smibet.domain.Odd;
import it.smibet.dto.OddDTO;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class OddMapper {


    public static Odd fromDTO(OddDTO oddDTO) {
        return Odd.builder()
                .dataEvent(oddDTO.getDataEvent())
                .prediction(oddDTO.getPrediction())
                .team(oddDTO.getTeam())
                .dateMatch(LocalDate.parse(oddDTO.getDataEvent()))
                .build();


    }

    public static List<Odd> fromDTO(List<OddDTO> oddDTO) {
        return oddDTO.stream()
                .map(OddMapper::fromDTO)
                .collect(Collectors.toList());
    }
    public static OddDTO fromEntity(Odd odd) {
        return OddDTO.builder()
                .dataEvent(odd.getDataEvent())
                .prediction(odd.getPrediction())
                .team(odd.getTeam())
                .build();


    }

    public static List<OddDTO> fromEntity(List<Odd> odds) {
        return odds.stream()
                .map(OddMapper::fromEntity)
                .collect(Collectors.toList());
    }

}
