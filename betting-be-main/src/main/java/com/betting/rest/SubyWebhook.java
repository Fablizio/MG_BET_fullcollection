package com.betting.rest;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/mgbet/payments")
public class SubyWebhook {


    private final String SECRET_KEY = System.getenv("SECRET_KEY");

    @PostMapping
    public void on() {

    }


}

