package com.betting.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class RaddoppioDTO {

    private long id;
    private String dataRaddoppio;
    private List<SchedinaDTO> odds;
    private boolean preso;
}

