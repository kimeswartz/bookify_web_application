package com.bookify.bookify_app.exception;

// ********************************************************************************************
// * GlobalExceptionHandler centralizes error handling across the application.                *
// * It catches exceptions, builds standardized ProblemDetail responses with HTTP status,     *
// * error details, request URI, and the correlation ID for traceability.                     *
// ********************************************************************************************

import com.bookify.bookify_app.util.CorrelationIdHolder;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.ErrorResponseException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import jakarta.servlet.http.HttpServletResponse;
import java.net.URI;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ProblemDetail handlerException( Exception ex, HttpServletRequest request) {
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;

        ProblemDetail problem = ProblemDetail.forStatusAndDetail(status, ex.getMessage());
        problem.setType(URI.create("https://bookify.dev/errors/internal-error"));
        problem.setTitle("Internal Server Error");
        problem.setProperty("Instance",request.getRequestURI());
        problem.setProperty("correlationID", CorrelationIdHolder.getId());

        return problem;
    }

    @ExceptionHandler(ErrorResponseException.class)
    public ProblemDetail handlerException( ErrorResponseException ex, HttpServletRequest request) {
        ProblemDetail problem = ex.getBody();
        problem.setProperty("instance",request.getRequestURI());
        problem.setProperty("correlationID", CorrelationIdHolder.getId());
        return problem;
    }
}