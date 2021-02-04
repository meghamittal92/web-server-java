package com.client.calorieserver.domain.dto;


import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.util.Set;

/**
 * Request object to Create a new user
 */
@Data
public class CreateUserRequest {

    //TODO custom validator
    @NotBlank
    private String username;

    //TODO custom validator
    @NotBlank
    private String password;

    private Boolean enabled;

    //TODO custom validator to check if role names exist
    private Set<String> roles;
}
