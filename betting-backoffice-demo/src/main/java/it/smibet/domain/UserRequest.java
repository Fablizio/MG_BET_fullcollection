package it.smibet.domain;

import it.smibet.types.RequestType;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UserRequest {


    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    Integer id;

    String paymentCode;

    LocalDate requestDate;

    @Enumerated(EnumType.STRING)
    RequestType requestType;

}
