package com.client.calorieserver.domain.dto.request;

import com.client.calorieserver.domain.validator.PasswordConstraint;
import com.client.calorieserver.domain.validator.UsernameConstraint;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;

@Data
public class RegisterUserRequest {

    @UsernameConstraint
    @NotBlank
    private String username;

    @PasswordConstraint
    @NotBlank
    private String password;

    @Min(0)
    private Integer expectedCaloriesPerDay;

    @Email
    @NotBlank
    private String email;
}