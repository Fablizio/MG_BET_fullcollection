package it.smibet.controller;

import it.smibet.dto.StatusDoublingDTO;
import it.smibet.service.StatusDoublingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/web/raddoppio")
public class StatusDoublingController {

    @Autowired
    StatusDoublingService statusDoublingService;

    @GetMapping
    public String getAllStatusRaddoppi(Model model) {

        model.addAttribute("status", statusDoublingService.getStatus());

        return "views/raddoppi";
    }


    @PostMapping
    public ResponseEntity<Void> update(@ModelAttribute("handligDoubling") StatusDoublingDTO statusDoublingDTO) {

        statusDoublingService.changeStatus(statusDoublingDTO);

        return ResponseEntity.ok().build();
    }


}
