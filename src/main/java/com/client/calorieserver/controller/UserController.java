package com.client.calorieserver.controller;

import com.client.calorieserver.domain.dto.CreateUserRequest;
import com.client.calorieserver.domain.dto.UpdateUserRequest;
import com.client.calorieserver.domain.dto.UserView;
import com.client.calorieserver.domain.mapper.UserMapper;
import com.client.calorieserver.domain.model.User;
import com.client.calorieserver.service.UserService;
import javassist.tools.web.BadHttpRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * Controller to provide operations on {@link User} object.
 */
@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
public class UserController {


    private final UserService userService;
    private final UserMapper userMapper;


    @GetMapping
    public List<UserView> findAll() {
        return userMapper.toUserView(userService.findAll());
    }

    @GetMapping(path = "/{username}")
    public UserView find(@PathVariable("username") String username) {

        final User user = userService.findByUsername(username);
        return userMapper.toUserView(userService.findByUsername(username));

    }

    @PostMapping(consumes = "application/json")
    public UserView create(@RequestBody @Valid CreateUserRequest createUserRequest) {

        User user = userMapper.toUser(createUserRequest);
        return userMapper.toUserView(userService.create(user));
    }

    @DeleteMapping(path = "/{id}")
    public void delete(@PathVariable("id") Long userId) {

        userService.deleteById(userId);
    }

    @PutMapping(path = "/{id}")
    public UserView update(@PathVariable("id") Long userId, @RequestBody @Valid UpdateUserRequest updateUserRequest) throws BadHttpRequest {

        User updatedUser = userMapper.toUser(updateUserRequest);
        return userMapper.toUserView(userService.updateById(userId, updatedUser));
    }

}
