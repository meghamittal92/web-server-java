package com.client.calorieserver.domain.dto.request;


import com.client.calorieserver.domain.mapper.CustomDateDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Data;


import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;


/**
 * Request object for a logged in user to
 * create his own calories.
 */
@Data
public class CreateUserCalorieRequest {

    @JsonDeserialize(using = CustomDateDeserializer.class)
    @NotNull
    LocalDateTime dateTime;

    @Min(0)
    Integer numCalories;

    @NotEmpty
    String mealDetails;
}
