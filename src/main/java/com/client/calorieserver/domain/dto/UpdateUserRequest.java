package com.client.calorieserver.domain.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * Request object to update an existing user.
 */
@Data
public class UpdateUserRequest {

    @NotBlank
    private String username;

    @NotBlank
    private String password;

    private boolean enabled;
}
