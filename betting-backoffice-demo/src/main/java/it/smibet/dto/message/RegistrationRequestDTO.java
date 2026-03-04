package it.smibet.dto.message;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class RegistrationRequestDTO extends TelegramUserDTO {

    String filePath;

}
