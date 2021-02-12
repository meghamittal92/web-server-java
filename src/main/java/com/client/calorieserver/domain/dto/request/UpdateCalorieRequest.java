package com.client.calorieserver.domain.dto.request;

import com.client.calorieserver.domain.mapper.CustomDateDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Data;

import javax.validation.constraints.Min;
import java.time.LocalDateTime;

/**
 * Request object to update an exiting calorie.
 */
@Data
public class UpdateCalorieRequest {

	@JsonDeserialize(using = CustomDateDeserializer.class)
	LocalDateTime dateTime;

	@Min(0)
	int numCalories;

	String mealDetails;

}
