package com.client.calorieserver.domain.exception;

/**
 * Enumeration of all client facing error messages and codes.
 */
public enum ApiError {

	CLIENT_ERROR("E0001", "Client error"), SERVER_ERROR("E0002", "Server error"), REDIRECTION("E0003",
			"Redirection error"),

	INVALID_INPUT("E0004", "Invalid Input"), INTERNAL_SERVER_ERROR("E0005", "Internal Server Error"), USER_NOT_FOUND(
			"E0006", "User not found"), USER_ALREADY_EXISTS("E0007",
					"User already exists"), CALORIE_NOT_FOUND("E0008", "Calorie not found"),

	HANDLER_NOT_FOUND("E0009", "Handler Not Found"), REQUEST_METHOD_NOT_SUPPORTED("E0010",
			"Request Method not supported"), MEDIA_TYPE_NOT_SUPPORTED("E0011", "Media Type not supported"),

	UNAUTHORIZED("E0012", "Unauthorized"), BAD_CREDENTIALS("E0013", "Bad Credentials"), INVALID_SEARCH_PARAMETER(
			"E0014", "Invalid search parameters"), CALORIE_FETCH_ERROR("E0015",
					"Unable to fetch calories. Please enter manually");

	private final String errorCode;

	private final String errorMessage;

	ApiError(String errorCode, String errorMessage) {

		this.errorCode = errorCode;
		this.errorMessage = errorMessage;
	}

	public String getErrorCode() {
		return errorCode;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

}
