package com.betting.security;


import com.betting.entity.User;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.authentication.AbstractAuthenticationToken;

@Getter
@Setter
public class JwtAuthenticationToken extends AbstractAuthenticationToken {

    private User authenticationDTO;
    private String jwtToken;

    JwtAuthenticationToken() {
        super(null);
    }

    public static JwtAuthenticationToken authenticated() {
        JwtAuthenticationToken jwtAuthenticationToken = new JwtAuthenticationToken();
        jwtAuthenticationToken.setAuthenticated(true);

        return jwtAuthenticationToken;
    }

    @Override
    public Object getCredentials() {
        return null;
    }

    @Override
    public Object getPrincipal() {
        return authenticationDTO;
    }


}

