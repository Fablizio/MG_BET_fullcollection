package it.betting.batch.entity;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Entity
@Data
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
    private boolean strana;
}
