package com.client.calorieserver.domain.dto;


import com.client.calorieserver.domain.validator.UsernameConstraint;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.util.Set;

/**
 * Request object to Create a new user
 */
@Data
public class CreateUserRequest {

    @UsernameConstraint
    @NotBlank
    private String username;

    //TODO custom validator
    @NotBlank
    private String password;


    private Set<String> roles;

    private Integer expectedCaloriesPerDay;
}
