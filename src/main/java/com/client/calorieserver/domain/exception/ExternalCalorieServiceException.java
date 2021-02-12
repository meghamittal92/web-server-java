package com.client.calorieserver.domain.exception;

/**
 * This exception is thrown when there is an error contacting an external
 * service to fetch calories.
 */
public class ExternalCalorieServiceException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public ExternalCalorieServiceException(String message) {
        super(message);
    }
}
