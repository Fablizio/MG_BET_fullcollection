package com.betting.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.time.LocalDate;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Link {

    @Id
    private Long id;

    private String facebook;

    private String instagram;

    private LocalDate dataPubblicazione;

}
