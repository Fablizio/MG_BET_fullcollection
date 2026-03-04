package it.smibet.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.time.LocalDate;

@Entity
@Data
@Builder
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
    private Boolean presa;
    private LocalDate dateMatch;
}
