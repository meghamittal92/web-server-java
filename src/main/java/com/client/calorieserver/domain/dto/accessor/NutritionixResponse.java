package com.client.calorieserver.domain.dto.accessor;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

/**
 * Response object from Nutrionix service.
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class NutritionixResponse {

	List<Food> foods;

	public Integer getCalories() {
		return foods.stream().mapToInt(food -> food.getCalories()).sum();
	}

}

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
class Food {

	@JsonProperty("food_name")
	String foodName;

	@JsonProperty("nf_calories")
	Integer calories;

}
