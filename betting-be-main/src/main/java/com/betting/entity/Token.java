package com.betting.entity;

import com.betting.enumeration.TokenStatus;
import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Token {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private TokenStatus status;

    @ManyToOne
    private User user;

    @ManyToOne
    private AiElaboration elaboration;

}
