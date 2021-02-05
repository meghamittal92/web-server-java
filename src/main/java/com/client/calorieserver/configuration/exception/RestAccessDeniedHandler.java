package com.client.calorieserver.configuration.exception;

import com.client.calorieserver.domain.dto.ErrorResponse;
import com.client.calorieserver.domain.exception.ApiError;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;


@Component
@RequiredArgsConstructor
public class RestAccessDeniedHandler implements AccessDeniedHandler {


    @Autowired
    ObjectMapper objectMapper;

    @Override
    public void handle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, AccessDeniedException e) throws IOException {
        final ErrorResponse errorResponse = new ErrorResponse(HttpStatus.UNAUTHORIZED,
                ApiError.UNAUTHORIZED.getErrorMessage(), ApiError.UNAUTHORIZED.getErrorCode());


        httpServletResponse.setStatus(HttpStatus.UNAUTHORIZED.value());

        httpServletResponse.setContentType(MediaType.APPLICATION_JSON_VALUE);
        httpServletResponse.setCharacterEncoding(StandardCharsets.UTF_8.name());
        httpServletResponse.getOutputStream()
                .println(objectMapper.writeValueAsString(errorResponse));
    }
}
