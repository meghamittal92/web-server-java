package com.client.calorieserver.domain.exception;


public class ExternalCalorieServiceException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public ExternalCalorieServiceException(String message) {
        super(message);
    }
}
