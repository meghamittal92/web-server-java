package com.client.calorieserver.util.security;

import com.client.calorieserver.domain.model.User;
import com.client.calorieserver.util.security.JWTUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.Authentication;


public class JWTUtilTest {

    private final static String username = "user_name";
    private final static String password = "password";
    private final static int expirationMs = 100000;
    JWTUtil jwtUtil;

    @Mock
    Authentication authentication;

    @BeforeEach
    void init(){
        MockitoAnnotations.openMocks(this);
        jwtUtil = new JWTUtil();
        org.springframework.test.util.ReflectionTestUtils.setField(jwtUtil, "jwtSecret", password);
        org.springframework.test.util.ReflectionTestUtils.setField(jwtUtil, "jwtExpirationMs", expirationMs);
    }

    @Test
    void creatAuthToken(){
        User user = new User();
        user.setUsername(username);
        Mockito.when(authentication.getPrincipal()).thenReturn(user);
        String token = jwtUtil.generateJwtToken(authentication);
        assert token != null;
    }

    @Test
    void getUserName(){

        User user = new User();
        user.setUsername(username);
        Mockito.when(authentication.getPrincipal()).thenReturn(user);
        String token = jwtUtil.generateJwtToken(authentication);
        assert token != null;

        assert jwtUtil.getUserNameFromJwtToken(token).equalsIgnoreCase(username);
    }

    @Test
    void validateAuthToken(){

        User user = new User();
        user.setUsername(username);
        Mockito.when(authentication.getPrincipal()).thenReturn(user);
        String token = jwtUtil.generateJwtToken(authentication);
        assert token != null;
        assert jwtUtil.validateJwtToken(token);

        org.springframework.test.util.ReflectionTestUtils.setField(jwtUtil, "jwtSecret", password+"random");
        token = jwtUtil.generateJwtToken(authentication);
        assert token != null;
        org.springframework.test.util.ReflectionTestUtils.setField(jwtUtil, "jwtSecret", password);

        assert !jwtUtil.validateJwtToken(token);

        org.springframework.test.util.ReflectionTestUtils.setField(jwtUtil, "jwtExpirationMs", -10000000);
        token = jwtUtil.generateJwtToken(authentication);
        assert token != null;
        assert !jwtUtil.validateJwtToken(token);
        org.springframework.test.util.ReflectionTestUtils.setField(jwtUtil, "jwtExpirationMs", expirationMs);
    }
}
