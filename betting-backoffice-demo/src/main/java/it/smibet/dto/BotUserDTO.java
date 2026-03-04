package it.smibet.dto;

import lombok.Data;

@Data
public class BotUserDTO {

    String type;
    String chatId;
    String code;
    String dataDiScadenza;
    String paymentCode;
}
