package com.client.calorieserver.domain.model.calorie.accessor;

import lombok.Data;

/**
 * Request object to Nutrionix service.
 */
@Data
public class NutritionixRequest {

	final String query;

}
