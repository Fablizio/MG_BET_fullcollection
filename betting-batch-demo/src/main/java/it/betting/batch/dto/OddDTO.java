package it.betting.batch.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OddDTO {

    private String idSite;
    private Long id;
    private String dataEvent;
    private String team;
    private double uno;
    private double x;
    private double due;
    private String prediction;
    private String result;
    private boolean presa;
    private String campionato;
    private double aggio;
    private int oci;
    private Date aggiornamentoPrediction;
    private double oldUno;
    private double oldX;
    private double oldDue;
    private double quotaInizialeUno;
    private double quotaInizialeX;
    private double quotaInizialeDue;
    private double first;
    private boolean error;
    private Date dateMatch;
    private String finalResult;
}
