package com.client.calorieserver.domain.exception;

public class InvalidSearchQueryException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public InvalidSearchQueryException(String message) {
        super(message);
    }
}
