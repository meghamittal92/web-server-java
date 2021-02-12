package com.client.calorieserver.domain.exception;

/**
 * This exception is thrown when an invalid search query is provided to any of the search
 * endpoints.
 */
public class InvalidSearchQueryException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public InvalidSearchQueryException(String message) {
		super(message);
	}

}
