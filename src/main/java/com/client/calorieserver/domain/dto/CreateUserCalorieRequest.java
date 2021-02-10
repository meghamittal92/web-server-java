package com.client.calorieserver.domain.dto;


import com.client.calorieserver.domain.mapper.CustomDateDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Data;


import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;


@Data
public class CreateUserCalorieRequest {

    @JsonDeserialize(using = CustomDateDeserializer.class)
    @NotNull
    LocalDateTime dateTime;

    Integer numCalories;

    @NotEmpty
    String mealDetails;
}
