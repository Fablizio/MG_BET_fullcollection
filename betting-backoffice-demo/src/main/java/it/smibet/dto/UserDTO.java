package it.smibet.dto;

import it.smibet.domain.PaymentsData;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO {

    Integer id;

    //    @NotEmpty(message = "Codice obbligatorio")
    String code;

    @NotEmpty(message = "Nickname obbligatorio")
    String nickname;

    @NotNull(message = "Importo pagato obbligatorio")
    Double payment;

    @NotEmpty(message = "Data di scadenza bbligatoria")
    String expiration;


    String token;

    String telegramSession;

    Boolean trialUsed;

    String username;

    UserRequestDTO lastUserRequestDTO;

    private String friendCode;

    private String discordUsername;

    @Builder.Default
    private List<PaymentsDataDTO> paymentsData = new ArrayList<>();


    // ---- Campi per update

    Boolean cleanLastRequest;

    int friends;


}
