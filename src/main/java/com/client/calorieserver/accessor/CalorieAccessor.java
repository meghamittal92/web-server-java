package com.client.calorieserver.accessor;

/**
 * Accessor class to fetch calories from an external
 * service.
 */
public interface CalorieAccessor {

    /**
     * Get calories from an external service
     * @param mealDetails details of the meal for which calories need to be fetched
     * @param userId The userId requesting the fetch
     * @return number of calories.
     */
    public Integer getCalories(final String mealDetails, final Long userId);

}
