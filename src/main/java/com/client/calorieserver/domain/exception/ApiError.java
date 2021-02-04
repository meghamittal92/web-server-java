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

    HANDLER_NOT_FOUND("E0007", "Handler Not Found"),
    REQUEST_METHOD_NOT_SUPPORTED("E0008", "Request Method not supported"),
    MEDIA_TYPE_NOT_SUPPORTED("E0009", "Media Type not supported"),

    UNAUTHORIZED("E0010", "Unauthorized"),
    BAD_CREDENTIALS("E0011", "Bad Credentials");


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
