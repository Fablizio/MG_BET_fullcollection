package com.betting.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SiteFeedDTO {
    private Long id;
    private String categoria;
}
