package com.betting.rest;

import com.betting.converter.Converter;
import com.betting.dto.CodeFriendRequestDTO;
import com.betting.dto.UserDTO;
import com.betting.entity.User;
import com.betting.service.TokenService;
import com.betting.service.impl.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/user/")
@Slf4j
public class UserRest {


    @Autowired
    UserService userService;

    @Autowired
    HttpServletRequest httpServletRequest;

    @Autowired
    private TokenService tokenService;

    @PutMapping("/update/discord-username/{discordUsername}")
    public ResponseEntity<UserDTO> updateDiscordUsername(@PathVariable String discordUsername) {
        User user = userService.updateDiscordUsername(discordUsername, httpServletRequest.getHeader("CODE"));
        return ResponseEntity.ok(Converter.convertFromEntity(user));
    }

    @GetMapping("/details")
    public ResponseEntity<UserDTO> getDetails() {
        User user = Optional.ofNullable(userService.findByCode(httpServletRequest.getHeader("CODE"))).orElseThrow(() -> new HttpClientErrorException(HttpStatus.UNAUTHORIZED));
        UserDTO userDto = Converter.convertFromEntity(user);
        userDto.setToken((int) tokenService.countAvailableTokens(user));
        return ResponseEntity.ok(userDto);
    }


    @PostMapping("/apply-friend-code")
    public ResponseEntity<Void> applyFriendCode(@RequestBody CodeFriendRequestDTO codeFriendRequestDTO) {
        userService.applyFriendCode(codeFriendRequestDTO.getFriendCode(), httpServletRequest);
        return ResponseEntity.ok().build();

    }

    @ExceptionHandler(HttpClientErrorException.class)
    public ResponseEntity<Object> handleException(HttpClientErrorException e) {
        Map<String, String> response = new HashMap<>();
        response.put("message", e.getStatusText());
        return ResponseEntity.status(e.getStatusCode()).body(response);
    }

}
