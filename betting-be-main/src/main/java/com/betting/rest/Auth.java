package com.betting.rest;

import com.betting.dto.ExpirationDTO;
import com.betting.entity.User;
import com.betting.service.IUserService;
import com.betting.util.BettingUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/auth")
public class Auth {


    @Autowired
    IUserService userService;

    @Autowired
    HttpServletRequest httpServletRequest;

    @PostMapping("/signin")
    public ResponseEntity<ExpirationDTO> auth() {
        User user = userService.findByCode(httpServletRequest.getHeader("CODE"));
        return ResponseEntity.ok(ExpirationDTO.builder().dateExpiration(BettingUtils.convertDateToString(user.getExpiration())).build());
    }
}
