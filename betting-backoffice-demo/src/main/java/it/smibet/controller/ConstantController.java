package it.smibet.controller;

import it.smibet.domain.Constant;
import it.smibet.service.ConstantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("constant")
public class ConstantController {

    @Autowired
    ConstantService constantService;

    @GetMapping
    public Constant findByCode(@RequestParam String code){
        return this.constantService.findByCode(code);
    }

}
