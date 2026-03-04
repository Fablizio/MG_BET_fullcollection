package com.betting.converter;

import com.betting.dto.ChangeOfOddResponseDTO;
import com.betting.entity.ChangeOfOdd;

import java.util.List;
import java.util.stream.Collectors;

public class ChangeOfOddMapper {


    public static List<ChangeOfOddResponseDTO> fromEntity(List<ChangeOfOdd> changeOfOddList) {
        return changeOfOddList.stream()
                .map(ChangeOfOddMapper::fromEntity)
                .collect(Collectors.toList());
    }

    public static ChangeOfOddResponseDTO fromEntity(ChangeOfOdd changeOfOdd) {
        return ChangeOfOddResponseDTO.builder()
                .uno(changeOfOdd.getUno())
                .x(changeOfOdd.getX())
                .due(changeOfOdd.getDue())
                .dateTime(changeOfOdd.getUpdateData().toString())
                .build();
    }


}
