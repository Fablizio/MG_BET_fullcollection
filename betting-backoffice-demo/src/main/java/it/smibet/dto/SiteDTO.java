package it.smibet.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SiteDTO {


    private String id;
    @NotEmpty(message = "Campo Site obbligatorio")
    private String site;
    @NotEmpty(message = "Campo Territorio obbligatorio")
    private String territorio;

    @NotEmpty(message = "Campo Campionato obbligatorio")
    private String campionato;

    private String attivo;

}
