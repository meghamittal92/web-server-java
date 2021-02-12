package com.client.calorieserver.domain.dto.response;

import com.client.calorieserver.domain.mapper.CustomDateSerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * Admin view of a calorie.
 * This contains the ID of the user to which the calorie is attached
 * apart from other calorie details.
 */
@Data
public class AdminCalorieView {
    private Long id;
    @JsonSerialize(using = CustomDateSerializer.class)
    LocalDateTime dateTime;
    int numCalories;
    String mealDetails;
    boolean isWithinLimit;
    private Long userId;
}
