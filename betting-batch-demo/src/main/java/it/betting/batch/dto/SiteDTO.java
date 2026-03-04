package it.betting.batch.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class SiteDTO {

    private Long id;
    private String site;
    private String territorio;
    private String campionato;
}
