package com.client.calorieserver.controller;

import com.client.calorieserver.domain.dto.ErrorResponse;
import com.client.calorieserver.domain.exception.ApiError;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;

/**
 * Custom Error controller for the application.
 */
@RestController
@SuppressWarnings("deprecation")
public class CustomErrorController implements ErrorController {


    @RequestMapping("${server.error.path}")
    public ResponseEntity<ErrorResponse> handleError(HttpServletRequest request) {
        Object statusCode = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);

        ApiError apiError = ApiError.SERVER_ERROR;
        ;
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        if (statusCode != null) {

            status = HttpStatus.valueOf(Integer.valueOf(statusCode.toString()));
            apiError = status.is4xxClientError() ? ApiError.CLIENT_ERROR : ApiError.SERVER_ERROR;

        }
        ErrorResponse errorResponse = new ErrorResponse(status, apiError.getErrorMessage(), apiError.getErrorCode());
        return ResponseEntity.status(errorResponse.getStatus()).body(errorResponse);
    }

    @Override
    @Deprecated
    public String getErrorPath() {
        return null;
    }
}
