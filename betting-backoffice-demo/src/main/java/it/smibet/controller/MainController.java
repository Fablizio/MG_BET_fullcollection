package it.smibet.controller;

import it.smibet.domain.User;
import it.smibet.dto.UserDTO;
import it.smibet.dto.UserListDTO;
import it.smibet.service.UserService;
import lombok.extern.apachecommons.CommonsLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@Controller
@CommonsLog
@RequestMapping("web")
public class MainController {


    @Autowired
    UserService userService;

    @GetMapping
    public String index(Model model) {
        UserListDTO userListDTO = userService.findAll();
        model.addAttribute("userList", userListDTO);
        return "views/index";
    }

    @GetMapping("newUser")
    public String newUser(Model model) {
        model.addAttribute("user", new User());
        return "views/new_user";
    }

    @GetMapping("delete/{idUser}")
    public String newUser(@PathVariable Integer idUser) {
        this.userService.delete(idUser);
        return "redirect:/";
    }

    @GetMapping("editUser/{idUser}")
    public String editUser(Model model, @PathVariable Integer idUser) {
        model.addAttribute("user", this.userService.findById(idUser));
        return "views/update_user";
    }

    @PostMapping("newUser")
    public String save(@ModelAttribute("user") @Valid UserDTO user, BindingResult bindingResult) {
        if (user.getCode() != null && this.userService.checkIfCodeExists(user))
            bindingResult.addError(new FieldError("user", "code", "Codice già esistente"));


        if (bindingResult.hasErrors()) {
            log.info("Validazione insert fallita");
            return "views/new_user";
        }

        userService.save(user, true);
        return "redirect:/";
    }

    @PostMapping("sendMessage/{userId}")
    public String sendMessage(@PathVariable Integer userId, @RequestParam String message){
        userService.sendMessage(userId, message);

        return "redirect:/";
    }

    @PostMapping("editUser")
    public String update(@ModelAttribute("user") @Valid UserDTO user, BindingResult bindingResult) {
        if (user.getCode() != null && user.getId() != null && this.userService.checkIfCodeWithOtherIdExists(user))
            bindingResult.addError(new FieldError("user", "code", "Codice già esistente"));

        if (bindingResult.hasErrors()) {
            log.info("Validazione update fallita");
            return "views/update_user";
        }

        userService.update(user);
        return "redirect:/";
    }


    @GetMapping("generaCodice/{userId}")
    public String generaCodice(@PathVariable Integer userId) {
        userService.generaCodice(userId);
        return "redirect:/";
    }

    @GetMapping("rinnovaCodice/{userId}")
    public String rinnovaCodice(@PathVariable Integer userId, Model model) {
        //userService.rinnovaCodice(userId);
        model.addAttribute("user",userService.detail(userId));
        return "views/detail";
    }
    @GetMapping("rinnovatoCodice/{userId}")
    public String rinnovatoCodice(@PathVariable Integer userId) {
        userService.rinnovatoCodice(userId);

        return "redirect:/";
    }



    @GetMapping("attivaTrial/{userId}")
    public String attivaTrial(@PathVariable Integer userId) {
        userService.attivaTrial(userId);
        return "redirect:/";
    }

    @PostMapping(value = "diniego/{userId}")
    public String diniego(@PathVariable Integer userId, @RequestParam String message) {
        userService.diniegoRichiesta(userId, message);
        return "redirect:/";
    }


    @ExceptionHandler({Exception.class})
    public String handle(Exception e) {
        log.error("########## ERRORE ###########", e);

        return "views/error";
    }

}


