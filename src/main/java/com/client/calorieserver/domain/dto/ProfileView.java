package com.client.calorieserver.domain.dto;


import lombok.Data;

@Data
public class ProfileView {

    private String username;
    private Long expectedCaloriesPerDay;
}
