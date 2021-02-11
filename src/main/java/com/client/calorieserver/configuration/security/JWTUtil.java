package com.client.calorieserver.configuration.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.InvalidClaimException;
import com.auth0.jwt.exceptions.SignatureVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.client.calorieserver.domain.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JWTUtil {
    private static final Logger logger = LoggerFactory.getLogger(JWTUtil.class);

    @Value("${app.jwtSecret}")
    private String jwtSecret;

    @Value("${spring.datasource.password}")
    private String pass;

    @Value("${app.jwtExpirationMs}")
    private int jwtExpirationMs;

    public String generateJwtToken(Authentication auth) {

        final String token = JWT.create()
                .withSubject(((User) auth.getPrincipal()).getUsername())
                .withExpiresAt(new Date(System.currentTimeMillis() + jwtExpirationMs))
                .sign(Algorithm.HMAC512(jwtSecret.getBytes()));

        return token;


    }

    public String getUserNameFromJwtToken(String authToken) {
        return JWT.require(Algorithm.HMAC512(jwtSecret))
                .build()
                .verify(authToken)
                .getSubject();
    }

    public boolean validateJwtToken(String authToken) {
        try {
            System.out.println("#####");
            DecodedJWT jwt = JWT.require(Algorithm.HMAC512(jwtSecret.getBytes()))
                    .build()
                    .verify(authToken);
            System.out.println(jwt.getClaims());
            return true;
        } catch (SignatureVerificationException e) {
            logger.error("Invalid JWT signature: {}", e.getMessage());
        } catch (TokenExpiredException e) {
            logger.error("JWT token is expired: {}", e.getMessage());
        } catch (InvalidClaimException e) {
            logger.error("Invalid JWT token: {}", e.getMessage());
        } catch (IllegalArgumentException ex) {
            logger.error("JWT claims string is empty - {}", ex.getMessage());
        }

        return false;
    }
}
