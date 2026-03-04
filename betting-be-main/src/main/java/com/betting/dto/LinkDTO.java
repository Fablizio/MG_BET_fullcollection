package com.betting.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class LinkDTO {

    private String facebook;
    private String instagram;
    private String dataPubblicazione;
    private boolean present;

}

