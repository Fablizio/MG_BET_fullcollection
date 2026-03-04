package com.betting.rest;

import com.betting.dto.CreateElaborationRequest;
import com.betting.entity.User;
import com.betting.service.TokenService;
import com.betting.service.impl.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/betting/elaborations")
@RequiredArgsConstructor
public class AiElaborationController {

    private final TokenService tokenService;
    private final HttpServletRequest httpServletRequest;
    private final UserService userService;

    @PostMapping
    public void create(@RequestBody CreateElaborationRequest request) {

        User currentUser = userService.findByCode(httpServletRequest.getHeader("CODE"));
        tokenService.createElaboration(
                currentUser,
                request.getMatchIds()
        );

    }

}