package com.betting.dto;

import com.betting.enumeration.ConditionType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class CreateAlertRequest {
    private Long matchId;
    private String esito;
    private Double quotaTarget;
    private ConditionType conditionType;
}
