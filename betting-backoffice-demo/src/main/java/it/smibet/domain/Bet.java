package it.smibet.domain;

import it.smibet.enumeration.BetType;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@ToString
@Entity
@Table
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Bet {


    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private LocalDate date;

    @Enumerated(EnumType.STRING)
    private BetType betType;

    @OneToMany(cascade = CascadeType.ALL)
    @ToString.Exclude
    private List<Odd> odds;

}
