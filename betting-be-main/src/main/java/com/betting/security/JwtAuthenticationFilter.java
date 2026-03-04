package com.betting.security;

import com.betting.entity.User;
import com.betting.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    public static final String CODE = "Code";


    @Autowired
    private JwtUtility jwtUtility;

    @Autowired
    IUserService userService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String code = request.getHeader(CODE);
        User user = userService.findByCode(code);

        if (user != null && user.getToken() != null && jwtUtility.validateJwtToken(user.getToken())) {
            SecurityContextHolder.getContext().setAuthentication(JwtAuthenticationToken.authenticated());
        }
        filterChain.doFilter(request, response);
    }


}
