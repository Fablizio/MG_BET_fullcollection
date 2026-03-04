package it.smibet.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class SiteResponseDTO {


    private String idUrlCampionato;
    private String nazione;
    private String lega;
    private String flagAbilitazione;

}
