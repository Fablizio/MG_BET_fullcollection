package it.smibet.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Table(name = "HANDLER_DOUBLING")
public class StatusDoubling {


    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private boolean activeSingleMatch;
    private boolean activeTwoMatch;
    private boolean activeDoublingChanceMatch;



}
