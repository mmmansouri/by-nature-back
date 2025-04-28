package com.bynature.adapters.in.web.exception;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.net.URI;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.Collectors;

public abstract class BaseExceptionHandler {

    private static final String API_BASE_URI = "https://api.bynature.com/errors/";

    // Shared registry to track handled exceptions across handlers
    private static final Set<Class<? extends Exception>> HANDLED_EXCEPTION_TYPES =
            ConcurrentHashMap.newKeySet();

    /**
     * Register an exception type as handled by a specific handler
     */
    protected static void registerHandledExceptionType(Class<? extends Exception> exceptionType) {
        HANDLED_EXCEPTION_TYPES.add(exceptionType);
    }

    /**
     * Check if an exception type is already handled by a specific handler
     */
    protected static boolean isExceptionTypeHandled(Class<? extends Exception> exceptionType) {
        return HANDLED_EXCEPTION_TYPES.stream()
                .anyMatch(handledType -> handledType.isAssignableFrom(exceptionType));
    }

    /**
     * Creates a standard problem detail with common fields
     */
    protected ProblemDetail createProblemDetail(HttpStatus status, String title, String detail, String errorType) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(status, detail);
        problem.setTitle(title);
        problem.setType(URI.create(API_BASE_URI + errorType));
        problem.setProperty("timestamp", Instant.now());
        return problem;
    }

    /**
     * Enhanced version that directly accepts errorType as string for cleaner calls
     */
    protected ProblemDetail createProblemDetail(HttpStatus status, String title, String detail, URI type) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(status, detail);
        problem.setTitle(title);
        problem.setType(type);
        problem.setProperty("timestamp", Instant.now());
        return problem;
    }

    /**
     * Processes validation errors from MethodArgumentNotValidException
     */
    protected ProblemDetail handleValidationException(MethodArgumentNotValidException ex) {
        Map<String, String> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .collect(Collectors.toMap(
                        FieldError::getField,
                        error -> error.getDefaultMessage() != null ? error.getDefaultMessage() : "Invalid value",
                        (existing, replacement) -> existing + "; " + replacement
                ));

        var problemDetail = createProblemDetail(
                HttpStatus.BAD_REQUEST,
                "Validation Error",
                "Validation failed for request parameters",
                "validation"
        );
        problemDetail.setProperty("errors", errors);

        return problemDetail;
    }

    /**
     * Standardized handler for MethodArgumentNotValidException with customizable messages
     */
    protected ResponseEntity<ProblemDetail> handleValidationException(
            MethodArgumentNotValidException ex,
            String title,
            String detail,
            String errorType) {

        // Register exception type
        registerHandledExceptionType(MethodArgumentNotValidException.class);

        var problemDetail = createProblemDetail(
                HttpStatus.BAD_REQUEST,
                title,
                detail,
                errorType
        );

        List<String> errors = ex.getBindingResult().getAllErrors()
                .stream()
                .map(error -> {
                    if (error instanceof FieldError fieldError) {
                        return fieldError.getField() + ": " + fieldError.getDefaultMessage();
                    }
                    return error.getDefaultMessage();
                })
                .toList();

        problemDetail.setProperty("validationErrors", errors);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(problemDetail);
    }

    /**
     * Processes constraint violation errors
     */
    protected ProblemDetail handleConstraintViolation(ConstraintViolationException ex) {
        Map<String, String> errors = ex.getConstraintViolations()
                .stream()
                .collect(Collectors.toMap(
                        violation -> violation.getPropertyPath().toString(),
                        ConstraintViolation::getMessage,
                        (existing, replacement) -> existing + "; " + replacement
                ));

        var problemDetail = createProblemDetail(
                HttpStatus.BAD_REQUEST,
                "Constraint Violation",
                "Constraint violations occurred",
                "constraint-violation"
        );
        problemDetail.setProperty("errors", errors);

        return problemDetail;
    }

    /**
     * Generic exception handler with customizable mapping function
     */
    protected <T extends Exception> ProblemDetail handleException(
            T exception,
            HttpStatus status,
            String title,
            String errorType,
            Function<T, Map<String, Object>> additionalPropertiesMapper) {

        // Register this exception type as handled
        registerHandledExceptionType(exception.getClass());

        var problemDetail = createProblemDetail(
                status,
                title,
                exception.getMessage(),
                errorType
        );

        if (additionalPropertiesMapper != null) {
            Map<String, Object> additionalProps = additionalPropertiesMapper.apply(exception);
            additionalProps.forEach(problemDetail::setProperty);
        }

        return problemDetail;
    }
}
