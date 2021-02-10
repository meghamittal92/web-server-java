package com.client.calorieserver.configuration.exception;


import java.util.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;

import com.client.calorieserver.domain.dto.response.ErrorResponse;
import com.client.calorieserver.domain.exception.*;
import com.client.calorieserver.domain.model.Calorie;
import com.client.calorieserver.domain.model.User;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.TypeMismatchException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

/**
 * Custom handler to translate exceptions into Error Response objects.
 * Uses codes and messages defined in {@link ApiError} class to propagate to client.
 */
@ControllerAdvice
public class CustomRestExceptionHandler extends ResponseEntityExceptionHandler {

    private final Logger logger = LogManager.getLogger(CustomRestExceptionHandler.class);

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(final MethodArgumentNotValidException ex, final HttpHeaders headers, final HttpStatus status, final WebRequest request) {
        final List<String> details = new ArrayList<String>();
        for (final FieldError error : ex.getBindingResult().getFieldErrors()) {
            details.add(error.getField() + ": " + error.getDefaultMessage());
        }
        for (final ObjectError error : ex.getBindingResult().getGlobalErrors()) {
            details.add(error.getObjectName() + ": " + error.getDefaultMessage());
        }
        final ErrorResponse errorResponse = buildErrorResponse(HttpStatus.BAD_REQUEST, ApiError.INVALID_INPUT, details);
        return handleExceptionInternal(ex, errorResponse, headers, errorResponse.getStatus(), request);
    }

    @Override
    protected ResponseEntity<Object> handleMissingServletRequestParameter(final MissingServletRequestParameterException ex, final HttpHeaders headers, final HttpStatus status, final WebRequest request) {

        final String details = String.format("%s parameter is missing", ex.getParameterName());
        ErrorResponse errorResponse = buildErrorResponse(HttpStatus.BAD_REQUEST, ApiError.INVALID_INPUT, List.of(details));
        return handleExceptionInternal(ex, errorResponse, headers, errorResponse.getStatus(), request);

    }


    @ExceptionHandler({ConstraintViolationException.class})
    protected ResponseEntity<Object> handleConstraintViolation(final ConstraintViolationException ex, final WebRequest request) {

        final List<String> details = new ArrayList<String>();
        for (final ConstraintViolation<?> violation : ex.getConstraintViolations()) {
            details.add(violation.getRootBeanClass().getName() + " " + violation.getPropertyPath() + ": " + violation.getMessage());
        }

        ErrorResponse errorResponse = buildErrorResponse(HttpStatus.BAD_REQUEST, ApiError.INVALID_INPUT, details);
        return ResponseEntity.status(errorResponse.getStatus()).body(errorResponse);
    }

    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(
            HttpMessageNotReadableException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {

        final String details = ex.getLocalizedMessage().substring(0, ex.getLocalizedMessage().indexOf(";"));
        ErrorResponse errorResponse = buildErrorResponse(HttpStatus.BAD_REQUEST, ApiError.INVALID_INPUT, List.of(details));
        return handleExceptionInternal(ex, errorResponse, headers, errorResponse.getStatus(), request);
    }

    @Override
    protected ResponseEntity<Object> handleTypeMismatch(final TypeMismatchException ex, final HttpHeaders headers, final HttpStatus status, final WebRequest request) {

        final String errorDetail = String.format("%s  value for should be of type %s", ex.getValue(), ex.getPropertyName(), ex.getRequiredType());

        final ErrorResponse errorResponse = buildErrorResponse(HttpStatus.BAD_REQUEST, ApiError.INVALID_INPUT, List.of(errorDetail));
        return handleExceptionInternal(ex, errorResponse, headers, errorResponse.getStatus(), request);
    }

    @ExceptionHandler({MethodArgumentTypeMismatchException.class})
    protected ResponseEntity<Object> handleMethodArgumentTypeMismatch(final MethodArgumentTypeMismatchException ex, final WebRequest request) {


        final String errorDetail = String.format("% should be of type %s", ex.getName(), ex.getRequiredType().getName());

        ErrorResponse errorResponse = buildErrorResponse(HttpStatus.BAD_REQUEST, ApiError.INVALID_INPUT, List.of(errorDetail));
        return ResponseEntity.status(errorResponse.getStatus()).body(errorResponse);

    }

    @Override
    protected ResponseEntity<Object> handleNoHandlerFoundException(
            NoHandlerFoundException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        String errorDetail = String.format("No handler found for %s : %s", ex.getHttpMethod(), ex.getRequestURL());

        ErrorResponse errorResponse = buildErrorResponse(HttpStatus.NOT_FOUND, ApiError.HANDLER_NOT_FOUND, List.of(errorDetail));
        return handleExceptionInternal(ex, errorResponse, headers, errorResponse.getStatus(), request);
    }


    @Override
    protected ResponseEntity<Object> handleHttpRequestMethodNotSupported(
            HttpRequestMethodNotSupportedException ex,
            HttpHeaders headers,
            HttpStatus status,
            WebRequest request) {
        StringBuilder builder = new StringBuilder();
        builder.append(ex.getMethod());
        builder.append(
                " method is not supported for this request. Supported methods are ");
        ex.getSupportedHttpMethods().forEach(t -> builder.append(t + " "));

        ErrorResponse errorResponse = buildErrorResponse(HttpStatus.METHOD_NOT_ALLOWED,
                ApiError.REQUEST_METHOD_NOT_SUPPORTED, List.of(builder.toString()));
        return handleExceptionInternal(ex, errorResponse, headers, errorResponse.getStatus(), request);
    }

    @Override
    protected ResponseEntity<Object> handleHttpMediaTypeNotSupported(
            HttpMediaTypeNotSupportedException ex,
            HttpHeaders headers,
            HttpStatus status,
            WebRequest request) {
        StringBuilder builder = new StringBuilder();
        builder.append(ex.getContentType());
        builder.append(" media type is not supported. Supported media types are ");
        ex.getSupportedMediaTypes().forEach(t -> builder.append(t + " "));

        ErrorResponse errorResponse = buildErrorResponse(HttpStatus.UNSUPPORTED_MEDIA_TYPE,
                ApiError.MEDIA_TYPE_NOT_SUPPORTED, List.of(builder.toString()));
        return handleExceptionInternal(ex, errorResponse, headers, errorResponse.getStatus(), request);

    }

    @ExceptionHandler({EntityAlreadyExistsException.class})
    protected ResponseEntity<Object> handleEntityAlreadyExistsException(EntityAlreadyExistsException ex) {

        ApiError apiError = ApiError.CLIENT_ERROR;

        if (User.class.getSimpleName().equals(ex.getEntityClass().getSimpleName())) {
            apiError = ApiError.USER_ALREADY_EXISTS;
        }
        ErrorResponse errorResponse = buildErrorResponse(HttpStatus.BAD_REQUEST, apiError, List.of(ex.getMessage()));
        return ResponseEntity.status(errorResponse.getStatus()).body(errorResponse);
    }

    @ExceptionHandler({EntityNotFoundException.class})
    protected ResponseEntity<Object> handleNotFoundException(EntityNotFoundException ex) {

        ApiError apiError = ApiError.CLIENT_ERROR;

        if (User.class.getSimpleName().equals(ex.getEntityClass().getSimpleName())) {
            apiError = ApiError.USER_NOT_FOUND;
        } else if (Calorie.class.getSimpleName().equals(ex.getEntityClass().getSimpleName())) {
            apiError = ApiError.CALORIE_NOT_FOUND;

        }
        ErrorResponse errorResponse = buildErrorResponse(HttpStatus.NOT_FOUND, apiError, List.of(ex.getMessage()));
        return ResponseEntity.status(errorResponse.getStatus()).body(errorResponse);

    }

    @ExceptionHandler({AccessDeniedException.class})
    protected ResponseEntity<Object> handleDisabledException(AccessDeniedException ex, final HttpServletRequest request) {
        logger.error("Authentication failed : {}\n", request.getRequestURI(), ex);

        ErrorResponse errorResponse = buildErrorResponse(HttpStatus.UNAUTHORIZED, ApiError.UNAUTHORIZED);
        return ResponseEntity.status(errorResponse.getStatus()).body(errorResponse);
    }

    @ExceptionHandler({BadCredentialsException.class})
    protected ResponseEntity<Object> handleDisabledException(BadCredentialsException ex, final HttpServletRequest request) {
        logger.error("Authentication failed : {}\n", request.getRequestURI(), ex);

        ErrorResponse errorResponse = buildErrorResponse(HttpStatus.UNAUTHORIZED, ApiError.BAD_CREDENTIALS, List.of("Incorrect Password"));
        return ResponseEntity.status(errorResponse.getStatus()).body(errorResponse);
    }

    @ExceptionHandler({InvalidSearchQueryException.class})
    protected ResponseEntity<Object> handleInvalidSearchQueryException(InvalidSearchQueryException ex, final HttpServletRequest request) {
        logger.error("Invalid search query : {}\n", request.getRequestURI(), ex);

        ErrorResponse errorResponse = buildErrorResponse(HttpStatus.BAD_REQUEST, ApiError.INVALID_SEARCH_PARAMETER);
        return ResponseEntity.status(errorResponse.getStatus()).body(errorResponse);
    }

    @ExceptionHandler({ExternalCalorieServiceException.class})
    protected ResponseEntity<Object> handleCalorieServiceException(ExternalCalorieServiceException ex, final HttpServletRequest request) {
        logger.error("Calorie Service Exception: {}\n", request.getRequestURI(), ex);

        ErrorResponse errorResponse = buildErrorResponse(HttpStatus.BAD_REQUEST, ApiError.CALORIE_FETCH_ERROR);
        return ResponseEntity.status(errorResponse.getStatus()).body(errorResponse);
    }

    @ExceptionHandler({Exception.class})
    protected ResponseEntity<Object> handleAll(Exception ex, final HttpServletRequest request) {
        logger.error("Internal Error {}\n", request.getRequestURI(), ex.getCause() != null ? ex.getCause() : ex);

        ErrorResponse errorResponse = buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, ApiError.INTERNAL_SERVER_ERROR);
        return ResponseEntity.status(errorResponse.getStatus()).body(errorResponse);
    }

    @Override
    protected ResponseEntity<Object> handleExceptionInternal(Exception ex, Object body, HttpHeaders headers, HttpStatus status, WebRequest request) {

        if (body == null) {

            ApiError apiError = status.is4xxClientError() ? ApiError.CLIENT_ERROR : ApiError.SERVER_ERROR;
            ErrorResponse errorResponse = buildErrorResponse(status, apiError);
            body = errorResponse;
        }

        return super.handleExceptionInternal(ex, body, headers, status, request);

    }

    private ErrorResponse buildErrorResponse(HttpStatus httpStatus, ApiError apiError, List<String> details) {


        return new ErrorResponse(httpStatus, apiError.getErrorMessage(), apiError.getErrorCode(), details);

    }

    private ErrorResponse buildErrorResponse(HttpStatus httpStatus, ApiError apiError) {

        return new ErrorResponse(httpStatus, apiError.getErrorMessage(), apiError.getErrorCode());


    }
}