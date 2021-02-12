package com.client.calorieserver.domain.dto.response;

import com.client.calorieserver.domain.mapper.CustomDateSerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * User view of his calorie.
 */
@Data
public class UserCalorieView {

    private Long id;
    @JsonSerialize(using = CustomDateSerializer.class)
    LocalDateTime dateTime;
    int numCalories;
    String mealDetails;
    boolean isWithinLimit;
}
