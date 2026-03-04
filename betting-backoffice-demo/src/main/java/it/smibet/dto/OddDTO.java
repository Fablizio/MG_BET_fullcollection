package it.smibet.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class OddDTO {

    private String team;
    private String dataEvent;
    private String time;
    private String prediction;
}
