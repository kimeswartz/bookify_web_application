package com.bookify.bookify_app.exception;

// ********************************************************************************************
// * GlobalExceptionHandler centralizes error handling across the application.                *
// * It catches exceptions, builds standardized ProblemDetail responses with HTTP status,     *
// * error details, request URI, and the correlation ID for traceability.                     *
// ********************************************************************************************

import com.bookify.bookify_app.service.ClinicService;
import com.bookify.bookify_app.util.CorrelationIdHolder;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.ErrorResponseException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.net.URI;

@ControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handles cases where a clinic could not be found for a given subdomain.
     *
     * Example scenario:
     *   Host: okand.minapp.se → no clinic in DB
     *
     * Response:
     *   {
     *     "type": "https://bookify.dev/errors/clinic-not-found",
     *     "title": "Clinic Not Found",
     *     "status": 404,
     *     "detail": "Clinic with domain okand not found",
     *     "instance": "/api/v1/admin/clinic",
     *     "correlationID": "abc123..."
     *   }
     *
     * WHY:
     *   - Keeps error responses consistent with ProblemDetail (RFC 7807).
     *   - Easier debugging because correlationID is always included.
     */

    @ExceptionHandler(ClinicService.ClinicNotFoundException.class)
    public ProblemDetail handleClinicNotFound(ClinicService.ClinicNotFoundException ex, HttpServletRequest request) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
        problem.setType(URI.create("https://bookify.dev/errors/clinic-not-found"));
        problem.setTitle("Clinic Not Found");
        problem.setProperty("instance", request.getRequestURI());
        problem.setProperty("correlationID", CorrelationIdHolder.getId());
        return problem;
    }

    /**
     * Fallback for all unhandled exceptions.
     * - Returns 500 Internal Server Error
     * - Useful for unexpected errors in business logic.
     *
     * WHY:
     *   - Guarantees a client always gets structured JSON instead of raw stacktrace.
     *   - Adds correlationID for backend log tracing.
     */

    @ExceptionHandler(Exception.class)
    public ProblemDetail handlerException(Exception ex, HttpServletRequest request) {
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;

        ProblemDetail problem = ProblemDetail.forStatusAndDetail(status, ex.getMessage());
        problem.setType(URI.create("https://bookify.dev/errors/internal-error"));
        problem.setTitle("Internal Server Error");
        problem.setProperty("instance", request.getRequestURI());
        problem.setProperty("correlationID", CorrelationIdHolder.getId());

        return problem;
    }

    /**
     * Handles Spring’s built-in ErrorResponseExceptions.
     * - These are often thrown by validation errors, missing params, etc.
     *
     * WHY:
     *   - Ensures framework-level errors still follow our ProblemDetail format.
     */

    @ExceptionHandler(ErrorResponseException.class)
    public ProblemDetail handlerException(ErrorResponseException ex, HttpServletRequest request) {
        ProblemDetail problem = ex.getBody();
        problem.setProperty("instance", request.getRequestURI());
        problem.setProperty("correlationID", CorrelationIdHolder.getId());
        return problem;
    }
}