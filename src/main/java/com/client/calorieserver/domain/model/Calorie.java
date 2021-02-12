package com.client.calorieserver.domain.model;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * Domain object representing a calorie.
 */
@Data
public class Calorie {

	private Long id;

	LocalDateTime dateTime;

	Integer numCalories;

	String mealDetails;

	Integer totalCaloriesForDay;

	boolean isWithinLimit;

	Long userId;

}
