package com.client.calorieserver.controller;

import com.client.calorieserver.domain.dto.response.ErrorResponse;
import com.client.calorieserver.domain.exception.ApiError;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;

/**
 * Custom Error controller for the application. Note that exceptions are mapped into error
 * responses by
 * {@link com.client.calorieserver.configuration.exception.CustomRestExceptionHandler}
 * This is provided to maintain error response symmetry in case of unexpected failures
 * during exception translation or somewhere else in the filter chain.
 */
@RestController
@SuppressWarnings("deprecation")
public class CustomErrorController implements ErrorController {

	private final Logger logger = LogManager.getLogger(CustomErrorController.class);

	@RequestMapping("${server.error.path}")
	public ResponseEntity<ErrorResponse> handleError(HttpServletRequest request) {
		logger.error("Error controller reached:", request.getRequestURI(),
				request.getAttribute(RequestDispatcher.ERROR_EXCEPTION));
		Object statusCode = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);

		ApiError apiError = ApiError.SERVER_ERROR;
		HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
		if (statusCode != null) {

			status = HttpStatus.valueOf(Integer.valueOf(statusCode.toString()));
			apiError = status.is4xxClientError() ? ApiError.CLIENT_ERROR
					: (status.is3xxRedirection() ? ApiError.REDIRECTION : ApiError.SERVER_ERROR);

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
