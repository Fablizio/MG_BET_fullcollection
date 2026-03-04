package it.smibet.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
public class Constant {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    String code;
    @Column(name = "valore")
    @Lob
    String value;
}
