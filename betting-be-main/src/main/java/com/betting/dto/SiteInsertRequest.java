package com.betting.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class SiteInsertRequest {

    private String campionato;
    private String territorio;
    private String link;
    private boolean active;


}
