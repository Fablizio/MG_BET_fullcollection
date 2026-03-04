package it.smibet.domain;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Builder
public class PaymentsData {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    @Lob
    private String base64Image;

    private String status;

    private LocalDate date;

    @ManyToOne
    private User user;

}
