package com.betting.dto;

import com.betting.enumeration.ConditionType;
import lombok.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OddDTO {

    private String id;
    private String dataEvent;
    private String team;
    private double uno;
    private double x;
    private double due;
    private String prediction;
    private String result;
    private boolean presa;
    private double quotaInizialeUno;
    private double quotaInizialeX;
    private String campionato;
    private double quotaInizialeDue;
    private String dataAggiornamento;
    private Double predictionConfidence;
    private String predictionNote;
    private double percentualeUno;
    private double percentualeX;
    private double percentualeDue;
    @Builder.Default
    private List<QuotaAlertResponse> quotaAlerts = new ArrayList<>();


    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    @Setter
    @Builder
    public static class QuotaAlertResponse {
        private ConditionType conditionType;
        private Double quotaTarget;
        private Long quotaAlertId;
        private String esito;
    }
}
