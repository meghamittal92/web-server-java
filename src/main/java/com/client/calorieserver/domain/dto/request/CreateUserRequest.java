package com.client.calorieserver.domain.dto.request;


import com.client.calorieserver.domain.validator.PasswordConstraint;
import com.client.calorieserver.domain.validator.UsernameConstraint;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import java.util.Set;

/**
 * Request object to Create a new user.
 */
@Data
public class CreateUserRequest {

    @UsernameConstraint
    @NotBlank
    private String username;

    @PasswordConstraint
    @NotBlank
    private String password;

    @Email
    @NotBlank
    private String email;

    private Set<String> roles;

    @Min(0)
    private Integer expectedCaloriesPerDay;
}
