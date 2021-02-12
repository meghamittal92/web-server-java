package com.client.calorieserver.controller;

import com.client.calorieserver.domain.dto.response.ProfileView;
import com.client.calorieserver.domain.dto.request.UpdateProfileRequest;
import com.client.calorieserver.domain.dto.db.UserDTO;
import com.client.calorieserver.domain.mapper.UserMapper;
import com.client.calorieserver.domain.model.Role;
import com.client.calorieserver.domain.model.User;
import com.client.calorieserver.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.annotation.security.RolesAllowed;
import javax.validation.Valid;

/**
 * Controller to provide operations on user profile
 * for a logged in user .
 */
@RestController
@RequestMapping(path = "${server.request.path.userProfile}")
@RequiredArgsConstructor
@RolesAllowed({Role.RoleConstants.USER_VALUE})
public class UserProfileController {


    private final UserService userService;
    private final UserMapper userMapper;


    @GetMapping
    public ProfileView read() {

        final Long userId = fetchUserIdFromAuth();

        return userMapper.toProfileView(userService.findById(userId));
    }

    @PatchMapping
    public ProfileView updateProfile( @RequestBody @Valid UpdateProfileRequest updateProfileRequest) {

        final Long userId = fetchUserIdFromAuth();
        final User existingUser = userService.findById(userId);
        return userMapper.toProfileView(userService.updateById(userId,  userMapper.updateUser(updateProfileRequest, existingUser)));
    }

    private Long fetchUserIdFromAuth() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return ((User) auth.getPrincipal()).getId();

    }

}
