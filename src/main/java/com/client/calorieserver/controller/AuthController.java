package com.client.calorieserver.controller;

import com.client.calorieserver.configuration.security.JWTUtil;
import com.client.calorieserver.domain.dto.CreateUserRequest;
import com.client.calorieserver.domain.dto.LoginRequest;
import com.client.calorieserver.domain.dto.UserView;
import com.client.calorieserver.domain.mapper.UserMapper;
import com.client.calorieserver.domain.model.User;
import com.client.calorieserver.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping(path = "api/v1/public")
@RequiredArgsConstructor
public class AuthController {


    private final UserService userService;
    private final UserMapper userMapper;
    private final AuthenticationManager authenticationManager;
    private final JWTUtil jwtUtil;

    @PostMapping("register")
    public UserView register(@RequestBody @Valid final CreateUserRequest createUserRequest) {

        User user = userMapper.toUser(createUserRequest);
        return userMapper.toUserView(userService.create(user));
    }

    @PostMapping("login")
    public ResponseEntity<UserView> login(@RequestBody @Valid final LoginRequest request) {
        final Authentication auth = authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));

        final String username = ((User) auth.getPrincipal()).getUsername();
        final User user = (User)userService.loadUserByUsername(username);

        final HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setBearerAuth(jwtUtil.generateJwtToken(auth));


        return ResponseEntity.ok().headers(httpHeaders).body(userMapper.toUserView(user));
    }
}
