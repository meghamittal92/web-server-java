package com.client.calorieserver.domain.dto.response;

import com.client.calorieserver.configuration.Constants;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;
import lombok.Getter;
import org.springframework.boot.web.servlet.error.DefaultErrorAttributes;
import org.springframework.http.HttpStatus;


import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

/**
 * Response object for error responses from all APIs.
 */
@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
public class ErrorResponse {

    private HttpStatus status;
    private String message;
    private String errorCode;
    @JsonSerialize(as = LocalDateTime.class)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Constants.DateConstants.DATE_TIME_FORMAT)
    private LocalDateTime timestamp;
    List<String> details;

    private ErrorResponse() {
        timestamp = LocalDateTime.now();
    }

    public ErrorResponse(HttpStatus status) {
        this();
        this.status = status;
    }

    public ErrorResponse(HttpStatus status, String message, String errorCode, List<String> details) {
        this();
        this.status = status;
        this.message = message;
        this.errorCode = errorCode;
        this.details = details;
    }

    public ErrorResponse(HttpStatus status, String message, String errorCode) {
        this();
        this.status = status;
        this.message = message;
        this.errorCode = errorCode;
    }

}