package it.smibet.dto;

import it.smibet.types.RequestType;
import lombok.Data;

import javax.persistence.*;
import java.time.LocalDate;

@Data
public class UserRequestDTO {

    Integer id;

    String paymentCode;

    String requestDate;

    RequestType requestType;

}
