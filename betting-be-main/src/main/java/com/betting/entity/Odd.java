package com.betting.entity;

import lombok.*;

import javax.persistence.*;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Builder
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Odd {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String team;
    private double uno;
    private double x;
    private double due;
    private String dataEvent;
    private String prediction;
    private double aggio;
    private int oci;
    private Date aggiornamentoPrediction;
    private double oldUno;
    private double oldX;
    private double oldDue;
    private boolean strana;
    private double quotaInizialeUno;
    private double quotaInizialeX;
    private double quotaInizialeDue;
    @Transient
    private double first;
    @Transient
    private boolean error;
    private Date dateMatch;
    @OneToOne
    private Site site;
    private String finalResult;
    private boolean presa;
    private Double predictionConfidence; // 0..100
    @Lob
    private String predictionNote;
    @OneToMany(mappedBy = "matchId", cascade = CascadeType.ALL)
    @Builder.Default
    private Set<QuotaAlert> quotaAlerts = new HashSet<>();

}
