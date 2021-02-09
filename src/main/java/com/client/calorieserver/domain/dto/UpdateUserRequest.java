package com.client.calorieserver.domain.dto;

import lombok.Data;

import java.util.Set;

@Data
public class UpdateUserRequest {


    private String username;
    private String password;
    private Set<String> roles;
    private Long expectedCaloriesPerDay;
}
