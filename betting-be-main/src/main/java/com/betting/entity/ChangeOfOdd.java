package com.betting.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.time.LocalDateTime;

@Builder
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
public class ChangeOfOdd {


    @Id
    private String id;

    private double uno;
    private double x;
    private double due;
    private LocalDateTime updateData;

    @ManyToOne
    private Odd odd;
}
