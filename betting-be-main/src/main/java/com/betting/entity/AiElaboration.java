package com.betting.entity;

import com.betting.enumeration.ElaborationStatus;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AiElaboration {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Chi ha fatto la richiesta
    @ManyToOne(optional = false)
    private User user;

    // Token utilizzati per questa elaborazione
    @OneToMany
    private List<Token> usedTokens;

    // Partite elaborate
    @ManyToMany
    private List<Odd> matches;

    @Enumerated(EnumType.STRING)
    private ElaborationStatus status;

    private LocalDateTime createdAt;

    @Column(columnDefinition = "TEXT")
    private String aiResult;
}