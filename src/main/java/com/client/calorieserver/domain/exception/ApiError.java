package com.client.calorieserver.domain.exception;

/**
 * Enumeration of all client facing error messages and codes.
 */
public enum ApiError {


    CLIENT_ERROR("E0001", "Client error"),
    SERVER_ERROR("E0002", "Server error"),

    INVALID_INPUT("E0003", "Invalid Input"),
    INTERNAL_SERVER_ERROR("E0004", "Internal Server Error"),
    USER_NOT_FOUND("E0005", "User not found"),
    USER_ALREADY_EXISTS("E0006", "User already exists"),
    CALORIE_NOT_FOUND("E0007", "Calorie not found"),

    HANDLER_NOT_FOUND("E0008", "Handler Not Found"),
    REQUEST_METHOD_NOT_SUPPORTED("E0009", "Request Method not supported"),
    MEDIA_TYPE_NOT_SUPPORTED("E0010", "Media Type not supported"),

    UNAUTHORIZED("E0011", "Unauthorized"),
    BAD_CREDENTIALS("E0012", "Bad Credentials"),
    INVALID_SEARCH_PARAMETER("E0013", "Invalid search parameters"),
    CALORIE_FETCH_ERROR("E0014", "Unable to fetch calories. Please enter manually");


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
