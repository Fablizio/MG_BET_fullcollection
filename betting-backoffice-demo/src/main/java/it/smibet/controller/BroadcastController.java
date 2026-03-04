package it.smibet.controller;

import it.smibet.dto.BroadcastMessageDTO;
import it.smibet.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/web")
public class BroadcastController {

    @Autowired
    UserService userService;

    @GetMapping("/broadcast")
    public String initBroadcast(Model model) {
        model.addAttribute("message", new BroadcastMessageDTO());
        return "views/broadcast";
    }

    @PostMapping("/broadcast")
    public String sendBroadcast(@ModelAttribute("message") BroadcastMessageDTO broadcastMessageDTO, Model model) {

        userService.messageBroadcast(broadcastMessageDTO);
        model.addAttribute("message", new BroadcastMessageDTO());
        return "views/broadcast";
    }


}
