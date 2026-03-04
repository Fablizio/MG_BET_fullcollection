package it.smibet.dto.message;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class AssociaCodiceMessageDTO extends TelegramUserDTO {

    String code;

}
