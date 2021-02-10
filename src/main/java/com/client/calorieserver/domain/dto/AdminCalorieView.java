package com.client.calorieserver.domain.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AdminCalorieView {
    private Long id;
    LocalDateTime dateTime;
    int numCalories;
    String mealDetails;
    boolean isWithinLimit;
    private Long userId;
}
