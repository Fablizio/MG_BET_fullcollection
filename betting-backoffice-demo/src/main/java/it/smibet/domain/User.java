package it.smibet.domain;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    @Builder.Default
    private List<PaymentsData> payments = new ArrayList<>();

    private String username;

    private String code;

    private String token;

    private LocalDate expiration;

    private String errorToken;

    private String indirizzoIp;

    private String browser;

    private String nickname;

    private Double payment;

    private String telegramSession;

    private String friendCode;

    private Boolean trialUsed;

    private String discordUsername;

    @ManyToOne(cascade = CascadeType.ALL)
    private UserRequest lastUserRequest;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "user")
    @Builder.Default
    private List<CodeFriend> friends = new ArrayList<>();
    @Builder.Default
    private boolean friendCodeActive = false;

    private boolean admin;


}
