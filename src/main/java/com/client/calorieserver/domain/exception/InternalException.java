package com.client.calorieserver.domain.exception;

import lombok.Getter;

@Getter
public class InternalException extends RuntimeException {

    private static final long serialVersionUID = 1L;
    public InternalException(String message) {
        super(message);
    }
}

