package com.betting.entity;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String username;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "user")
    @Builder.Default
    private List<CodeFriend> friends = new ArrayList<>();

    private String code;

    private String discordUsername;

    private String token;

    private LocalDate expiration;

    private String errorToken;

    private String indirizzoIp;

    private String browser;

    private String nickname;

    private Double payment;

    private String friendCode;

    private boolean friendCodeActive;

    private String telegramSession;

}
