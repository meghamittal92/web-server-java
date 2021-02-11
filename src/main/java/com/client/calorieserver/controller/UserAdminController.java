package com.client.calorieserver.controller;

import com.client.calorieserver.domain.dto.request.CreateUserRequest;
import com.client.calorieserver.domain.dto.request.UpdateUserRequest;
import com.client.calorieserver.domain.dto.db.UserDTO;
import com.client.calorieserver.domain.dto.response.UserView;
import com.client.calorieserver.domain.mapper.UserMapper;
import com.client.calorieserver.domain.model.Role;
import com.client.calorieserver.domain.model.User;
import com.client.calorieserver.service.UserService;
import com.client.calorieserver.util.SpecificationBuilder;
import com.client.calorieserver.util.UserDTOSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import javax.annotation.security.RolesAllowed;
import javax.validation.Valid;

/**
 * Controller to provide operations on {@link UserDTO} object.
 */
@RestController
@RequestMapping(path = "${server.request.path.users}")
@RequiredArgsConstructor
@RolesAllowed({Role.RoleConstants.ADMIN_VALUE, Role.RoleConstants.USER_MANAGER_VALUE})
public class UserAdminController {


    private final UserService userService;
    private final UserMapper userMapper;


    @GetMapping
    public Page<UserView> findAll(@RequestParam(value = "search", required = false) final String search, final Pageable pageable) {

        SpecificationBuilder<UserDTO> specificationBuilder = new SpecificationBuilder<>();
        if (search != null)
            specificationBuilder.with(search);

        return userService.findAll(specificationBuilder.build(UserDTOSpecification::new), pageable).map(userMapper::toUserView);
    }

    @GetMapping(path = "/{id}")
    public UserView find(@PathVariable("id") Long userId) {
        return userMapper.toUserView(userService.findById(userId));
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
