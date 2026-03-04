package com.betting.security;

import com.betting.entity.User;
import io.jsonwebtoken.*;
import lombok.extern.apachecommons.CommonsLog;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
@CommonsLog
public class JwtUtility {


    @Value("${jwtSecret}")
    private String jwtSecret;

    @Value("${jwtExpiration}")
    private Long jwtExpiration;


    public String getSubject(String jwtToken){
        return Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(jwtToken).getBody().getSubject();
    }


    public Date getExpiration(String jwtToken){
        return Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(jwtToken).getBody().getExpiration();
    }

    public String generateToken(User authenticationDTO) {
        return Jwts.builder()
                .setSubject(authenticationDTO.getCode())
                .setIssuedAt(new Date())
                .setExpiration(new Date((new Date()).getTime() + jwtExpiration))
                .signWith(SignatureAlgorithm.HS512, jwtSecret)
                .compact();
    }



    public boolean validateJwtToken(String authToken) {
        try {
            Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(authToken);
            return true;
        } catch (SignatureException e) {
            log.error("Invalid JWT signature -> Message: {} ", e);
        } catch (MalformedJwtException e) {
            log.error("Invalid JWT token -> Message: {}", e);
        } catch (ExpiredJwtException e) {
            log.error("Expired JWT token -> Message: {}", e);
        } catch (UnsupportedJwtException e) {
            log.error("Unsupported JWT token -> Message: {}", e);
        } catch (IllegalArgumentException e) {
            log.error("JWT claims string is empty -> Message: {}", e);
        }

        return false;
    }

    public String generateTokenWithCustomDateExpire(User user, Date date) {

        return Jwts.builder()
                .setSubject(user.getCode())
                .setIssuedAt(new Date())
                .setExpiration(new Date( date.getTime()))
                .signWith(SignatureAlgorithm.HS512, jwtSecret)
                .compact();

    }
}
