package it.smibet.controller;

import it.smibet.dto.TelegramMessageRequestDTO;
import it.smibet.dto.UserDTO;
import it.smibet.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/api/users")
@CrossOrigin
public class UserApi {


    @Autowired
    private UserService userService;

    @GetMapping
    public List<UserDTO> getAll() {
        return userService.findAll().getUsers();
    }

    @PostMapping
    public ResponseEntity<Void> save(@RequestBody UserDTO user) {
        userService.save(user, true);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PutMapping
    public ResponseEntity<Void> update(@RequestBody UserDTO user) {
        userService.update(user);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Void> delete(@PathVariable(value = "id") String id) {
        userService.delete(Integer.parseInt(id));
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/{userId}/sendMessage")
    public ResponseEntity<Void> sendMessage(@PathVariable Integer userId, @RequestBody TelegramMessageRequestDTO telegramMessageRequestDTO) {
        userService.sendMessage(userId, telegramMessageRequestDTO.getMessage());
        return ResponseEntity.ok().build();
    }
}
