package com.client.calorieserver.domain.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class RegisterUserRequest {

    //TODO custom validator
    @NotBlank
    private String username;

    //TODO custom validator
    @NotBlank
    private String password;

    private Integer expectedCaloriesPerDay;
}