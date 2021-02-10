package com.client.calorieserver.domain.dto;

import com.client.calorieserver.domain.validator.PasswordConstraint;
import com.client.calorieserver.domain.validator.UsernameConstraint;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class RegisterUserRequest {

    @UsernameConstraint
    @NotBlank
    private String username;

    @PasswordConstraint
    @NotBlank
    private String password;

    private Integer expectedCaloriesPerDay;
}