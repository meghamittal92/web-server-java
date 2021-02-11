package com.client.calorieserver.domain.dto.request;


import com.client.calorieserver.domain.mapper.CustomDateDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
public class CreateCalorieRequest {


    @JsonDeserialize(using = CustomDateDeserializer.class)
    @NotNull
    LocalDateTime dateTime;

    @Min(0)
    Integer numCalories;

    @NotEmpty
    String mealDetails;

    @NotNull
    Long userId;
}
