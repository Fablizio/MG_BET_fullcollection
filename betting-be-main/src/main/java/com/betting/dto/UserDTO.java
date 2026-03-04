package com.betting.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserDTO {

    private String nickName;
    private String username;
    private String code;
    private String expireDate;
    private String friendCode;
    private boolean friendCodeActive;
    private boolean subscription;
    private String discordUsername;
    private int token;


}
