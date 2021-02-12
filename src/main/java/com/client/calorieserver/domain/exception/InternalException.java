package com.client.calorieserver.domain.exception;

import lombok.Getter;

/**
 * This exception is thrown when there is an unexpected error in the server.
 */
@Getter
public class InternalException extends RuntimeException {

    private static final long serialVersionUID = 1L;
    public InternalException(String message) {
        super(message);
    }
}

