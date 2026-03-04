package com.betting.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class SiteDTO {

    private Long id;
    private String campionato;
    private String url;
    private boolean active;


}
