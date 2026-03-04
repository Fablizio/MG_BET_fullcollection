package it.smibet.dto.message;

import lombok.Data;

@Data
public class TelegramUserDTO {

    String telegramSession;
    String nickname;
    String username;

    public TelegramUserDTO() {
    }

    public TelegramUserDTO(String telegramSession, String nickname, String username) {
        this.telegramSession = telegramSession;
        this.nickname = nickname;
        this.username = username;
    }
}
