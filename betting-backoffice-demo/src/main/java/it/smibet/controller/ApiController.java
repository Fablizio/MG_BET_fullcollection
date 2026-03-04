package it.smibet.controller;

import it.smibet.dto.BroadcastMessageDTO;
import it.smibet.dto.PaymentsDataDTO;
import it.smibet.dto.UserDTO;
import it.smibet.dto.message.*;
import it.smibet.exception.HttpStatusCodeException;
import it.smibet.service.UserService;
import lombok.extern.apachecommons.CommonsLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api")
@CommonsLog
public class ApiController {

    @Autowired
    UserService userService;

    @PostMapping("/sendImage")
    public ResponseEntity<Void> sendImage(@RequestBody PaymentsDataDTO data) {
        try {
            userService.processImage(data);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("Error processing image", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("code/{code}")
    public DataScadenzaDTO getDataScadenzaByCode(@PathVariable String code) {
        return this.userService.findDataScadenzaByCode(code, "yyyy-MM-dd");
    }

    @GetMapping("session/{code}")
    public DataScadenzaDTO getDataScadenzaByTelegramSession(@PathVariable String code) {
        return this.userService.findDataScadenzaByTelegramSession(code, "yyyy-MM-dd");
    }

    @GetMapping("chatId/{chatId}")
    public ResponseEntity<UserDTO> getUserByChatId(@PathVariable String chatId) {
        return ResponseEntity.ok(this.userService.findByTelegramSession(chatId));
    }

    @PostMapping("code")
    public ResponseEntity<Void> createNewRegistrationRequest(@RequestBody RegistrationRequestDTO registrationRequestDTO) throws Exception {
        this.userService.createNewRegistrationRequest(registrationRequestDTO);
        return ResponseEntity.ok().build();
    }

    @PostMapping("trial")
    public ResponseEntity<TrialCodeResponseDTO> createNewTrialRequest(@RequestBody TelegramUserDTO telegramUserDTO) {
        return ResponseEntity.ok(this.userService.createNewTrialRequest(telegramUserDTO));
    }

    @CrossOrigin
    @PutMapping("code")
    public ResponseEntity<Void> createNewRinnovoRequest(@RequestBody RinnovoRequestDTO rinnovoRequestDTO) throws Exception {
        this.userService.createNewRinnovoRequest(rinnovoRequestDTO);
        return ResponseEntity.ok().build();
    }


    @PostMapping("sendBroadcast")
    public ResponseEntity<Void> sendBroadcast(BroadcastMessageDTO message) {

        this.userService.messageBroadcast(message);

        return ResponseEntity.ok().build();
    }

    @PostMapping("associaCodice")
    public ResponseEntity<UserDTO> associaCodice(@RequestBody AssociaCodiceMessageDTO associaCodiceMessageDTO){
        return ResponseEntity.ok(this.userService.associaCodice(associaCodiceMessageDTO));
    }


    @ExceptionHandler({HttpStatusCodeException.class})
    public ResponseEntity handleException(HttpStatusCodeException e) {
        log.error("Errore Status Code: ", e);
        return ResponseEntity.status(e.getStatusCode()).body(e.getStatusText());
    }

    @ExceptionHandler({Exception.class})
    public ResponseEntity handleException(Exception e) {
        log.error("Errore Generico: ", e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
    }


}
