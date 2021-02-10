package com.client.calorieserver.domain.dto;


import com.client.calorieserver.domain.mapper.CustomDateDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Data;

import java.time.LocalDateTime;


@Data
public class UpdateCalorieRequest {

    @JsonDeserialize(using = CustomDateDeserializer.class)
    LocalDateTime dateTime;

    int numCalories;

    String mealDetails;
}
