package com.client.calorieserver.controller;

import com.client.calorieserver.configuration.security.JWTUtil;
import com.client.calorieserver.domain.dto.request.LoginRequest;
import com.client.calorieserver.domain.dto.request.RegisterUserRequest;
import com.client.calorieserver.domain.dto.response.ProfileView;
import com.client.calorieserver.domain.mapper.UserMapper;
import com.client.calorieserver.domain.model.User;
import com.client.calorieserver.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping(path = "${server.request.path.public}")
@RequiredArgsConstructor
public class AuthController {


    private final UserService userService;
    private final UserMapper userMapper;
    private final AuthenticationManager authenticationManager;
    private final JWTUtil jwtUtil;

    @PostMapping("${server.request.endpoint.registerUser}")
    public ProfileView register(@RequestBody @Valid final RegisterUserRequest registerUserRequest) {

        User user = userMapper.toUser(registerUserRequest);
        return userMapper.toProfileView(userService.create(user));
    }

    @PostMapping("${server.request.endpoint.loginUser}")
    public ResponseEntity<ProfileView> login(@RequestBody @Valid final LoginRequest request) {
        final Authentication auth = authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));

        final String username = ((User) auth.getPrincipal()).getUsername();
        final User user = (User) userService.loadUserByUsername(username);

        final HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setBearerAuth(jwtUtil.generateJwtToken(auth));


        return ResponseEntity.ok().headers(httpHeaders).body(userMapper.toProfileView(user));
    }
}
