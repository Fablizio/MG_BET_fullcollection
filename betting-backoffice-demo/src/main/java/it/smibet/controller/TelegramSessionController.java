package it.smibet.controller;

import it.smibet.dto.TelegramSessionResponse;
import it.smibet.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/api")
public class TelegramSessionController {

    @Autowired
    private UserService userService;


    @GetMapping(value = "/telegram-session")
    public ResponseEntity<TelegramSessionResponse> telegramSession() {
        TelegramSessionResponse respone = userService.getActiveTelegramSession();
        return ResponseEntity.ok(respone);
    }

}
