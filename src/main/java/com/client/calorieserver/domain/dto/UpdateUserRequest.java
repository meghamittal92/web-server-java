package com.client.calorieserver.domain.dto;

import com.client.calorieserver.domain.validator.PasswordConstraint;
import com.client.calorieserver.domain.validator.UsernameConstraint;
import com.sun.istack.Nullable;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.Set;

@Data
public class UpdateUserRequest {


    @UsernameConstraint
    private String username;

    @PasswordConstraint
    private String password;
    private Set<String> roles;
    private Integer expectedCaloriesPerDay;
    @Email
    private String email;
}
