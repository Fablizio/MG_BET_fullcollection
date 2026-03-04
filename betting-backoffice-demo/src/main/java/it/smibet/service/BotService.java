package it.smibet.service;

import it.smibet.dto.BotUserDTO;
import it.smibet.dto.BroadcastMessageDTO;
import it.smibet.dto.UserDTO;
import it.smibet.dto.message.DiniegoDTO;
import it.smibet.dto.message.SendMessageDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.RequestEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URI;

@Service
public class BotService {

    @Autowired
    RestTemplate restTemplate;

    @Value("${bot.baseurl}")
    String botBaseurl;

    @Value("${bot.esitoRichiesta}")
    String esitoRichiestaPath;

    @Value("${bot.broadcast}")
    String broadcastMessage;

    @Value("${bot.sendMessage}")
    String userMessage;

    public static final String CREATION_TYPE = "NEW";
    public static final String RENEW_TYPE = "RENEW";
    public static final String TRIAL_TYPE = "TRIAL";

    public void sendCreationCodeToBot(UserDTO userDTO) {
        BotUserDTO botUserDTO = buildBotUser(userDTO);
        botUserDTO.setType(CREATION_TYPE);

        exchangeRequest(botUserDTO);
    }

    public void sendRenewCodeToBot(UserDTO userDTO) {
        BotUserDTO botUserDTO = buildBotUser(userDTO);
        botUserDTO.setType(RENEW_TYPE);

        exchangeRequest(botUserDTO);
    }

    public void sendTrialCodeToBot(UserDTO userDTO) {
        BotUserDTO botUserDTO = buildBotUser(userDTO);
        botUserDTO.setType(TRIAL_TYPE);

        exchangeRequest(botUserDTO);
    }

    public void sendDiniego(DiniegoDTO diniegoDTO) {
        exchangeRequest(diniegoDTO);
    }

    private void exchangeRequest(BotUserDTO botUserDTO) {
        RequestEntity<BotUserDTO> requestEntity = RequestEntity.post(URI.create(botBaseurl + esitoRichiestaPath)).body(botUserDTO);
        this.restTemplate.exchange(requestEntity, Void.class);
    }

    private void exchangeRequest(DiniegoDTO diniegoDTO) {
        RequestEntity<DiniegoDTO> requestEntity = RequestEntity.post(URI.create(botBaseurl + esitoRichiestaPath)).body(diniegoDTO);
        this.restTemplate.exchange(requestEntity, Void.class);
    }

    private void exchangeRequest(BroadcastMessageDTO broadcastMessageDTO) {
        RequestEntity<BroadcastMessageDTO> requestEntity = RequestEntity.post(URI.create(botBaseurl + broadcastMessage)).body(broadcastMessageDTO);
        this.restTemplate.exchange(requestEntity, Void.class);
    }

    private void exchangeRequest(SendMessageDTO sendMessageDTO) {
        RequestEntity<SendMessageDTO> requestEntity = RequestEntity.post(URI.create(botBaseurl + userMessage)).body(sendMessageDTO);
        this.restTemplate.exchange(requestEntity, Void.class);
    }


    private BotUserDTO buildBotUser(UserDTO userDTO) {
        BotUserDTO result = new BotUserDTO();

        result.setChatId(userDTO.getTelegramSession());
        result.setCode(userDTO.getCode());
        result.setDataDiScadenza(userDTO.getExpiration());

        if (userDTO.getLastUserRequestDTO() != null)
            result.setPaymentCode(userDTO.getLastUserRequestDTO().getPaymentCode());

        return result;
    }

    public void sendBroadcast(BroadcastMessageDTO message) {
        exchangeRequest(message);
    }

    public void sendMessage(SendMessageDTO sendMessageDTO) {
        exchangeRequest(sendMessageDTO);
    }
}
