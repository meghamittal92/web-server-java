package com.client.calorieserver.domain.dto;


import lombok.Data;


@Data
public class UpdateProfileRequest {

    private Long expectedCaloriesPerDay;
}
