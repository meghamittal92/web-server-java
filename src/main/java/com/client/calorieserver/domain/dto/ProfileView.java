package com.client.calorieserver.domain.dto;


import lombok.Data;


@Data
public class ProfileView {

    private String username;
    private Integer expectedCaloriesPerDay;
    private String email;
}
