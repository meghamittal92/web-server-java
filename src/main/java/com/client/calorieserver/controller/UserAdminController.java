package com.client.calorieserver.controller;

import com.client.calorieserver.domain.dto.CreateUserRequest;
import com.client.calorieserver.domain.dto.UpdateCalorieRequest;
import com.client.calorieserver.domain.dto.UpdateUserRequest;
import com.client.calorieserver.domain.dto.db.UserDTO;
import com.client.calorieserver.domain.dto.UserView;
import com.client.calorieserver.domain.mapper.UserMapper;
import com.client.calorieserver.domain.model.Role;
import com.client.calorieserver.domain.model.User;
import com.client.calorieserver.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.annotation.security.RolesAllowed;
import javax.validation.Valid;

/**
 * Controller to provide operations on {@link UserDTO} object.
 */
@RestController
@RequestMapping(path = "/api/v1/users")
@RequiredArgsConstructor
@RolesAllowed({Role.RoleConstants.ADMIN_VALUE, Role.RoleConstants.USER_MANAGER_VALUE})
public class UserAdminController {


    private final UserService userService;
    private final UserMapper userMapper;


    @GetMapping
    public Page<UserView> findAll(final Pageable pageable) {
        return userService.findAll(pageable).map(userMapper::toUserView);
    }

    @GetMapping(path = "/{username}")
    public UserView find(@PathVariable("username") String username) {
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
    public UserView replace(@PathVariable("id") Long userId, @RequestBody @Valid CreateUserRequest createUserRequest) {

        User updatedUser = userMapper.toUser(createUserRequest);
        return userMapper.toUserView(userService.replaceById(userId, updatedUser));
    }


    @PatchMapping(path = "/{id}")
    public UserView update(@PathVariable("id") Long userId, @RequestBody @Valid UpdateUserRequest updateUserRequest) {

        User originalUser = userService.findById(userId);

        User updatedUser = userMapper.updateUser(updateUserRequest, originalUser);
        return userMapper.toUserView(userService.updateById(userId, updatedUser));
    }

}
