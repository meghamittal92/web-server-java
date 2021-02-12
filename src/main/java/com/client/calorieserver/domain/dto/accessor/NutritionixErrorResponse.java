package com.client.calorieserver.domain.dto.accessor;

import lombok.Data;

/**
 * Error response object from Nutrionix service.
 */
@Data
public class NutritionixErrorResponse {

	final String message;

	final String id;

}
