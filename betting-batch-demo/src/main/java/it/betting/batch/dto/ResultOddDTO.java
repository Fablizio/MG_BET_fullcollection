package it.betting.batch.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ResultOddDTO {


    private String team;
    private String result;
}
