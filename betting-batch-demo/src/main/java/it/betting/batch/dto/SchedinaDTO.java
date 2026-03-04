package it.betting.batch.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SchedinaDTO {

    private String type;
    private String campionato;
    private String team;
    private String prediction;
    private double quota;
    private String dateMatch;
    private boolean presa;
}
