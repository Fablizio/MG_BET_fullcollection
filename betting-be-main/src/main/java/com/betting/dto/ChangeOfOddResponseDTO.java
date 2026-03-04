package com.betting.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChangeOfOddResponseDTO {


    private double uno;
    private double x;
    private double due;
    private String dateTime;

}
