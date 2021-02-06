package com.client.calorieserver.domain.model;


import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Calorie {

    private Long id;
    LocalDateTime dateTime;
    int numCalories;
    String details;
    boolean isWithinLimit;
    private Long userId;
}
