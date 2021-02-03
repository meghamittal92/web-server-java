package com.client.calorieserver.domain.dto;


import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * Request object to Create a new user
 */
@Data
public class CreateUserRequest {

    @NotBlank
    private String username;

    @NotBlank
    private String password;

    private boolean enabled;
}
