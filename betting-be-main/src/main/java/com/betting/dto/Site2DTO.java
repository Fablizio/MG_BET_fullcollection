package com.betting.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
@Builder
public class Site2DTO {

    private String territorio;
    private List<SiteDTO> sites;

}
