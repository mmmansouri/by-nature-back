package com.bynature.adapters.in.web.customer;

import com.bynature.adapters.in.web.exception.BaseExceptionHandler;
import com.bynature.domain.exception.CustomerNotFoundException;
import com.bynature.domain.exception.CustomerValidationException;
import com.bynature.domain.exception.PhoneValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice(assignableTypes = {CustomerController.class})
@Order(1) // Higher priority than GlobalExceptionHandler
public class CustomerControllerExceptionHandler extends BaseExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(CustomerControllerExceptionHandler.class);

    // Register managed exception types in static initializer
    static {
        registerHandledExceptionType(CustomerNotFoundException.class);
        registerHandledExceptionType(CustomerValidationException.class);
    }

    @ExceptionHandler(CustomerNotFoundException.class)
    public ResponseEntity<ProblemDetail> handleCustomerNotFound(CustomerNotFoundException ex) {
        log.error("Customer not found: {}", ex.getMessage());

        var problem = handleException(
                ex,
                HttpStatus.NOT_FOUND,
                "Customer Not Found",
                "customers/not-found",
                exception -> ex.getCustomerId() != null
                        ? Map.of("customerId", ex.getCustomerId())
                        : Map.of()
        );

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(problem);
    }

    @ExceptionHandler({CustomerValidationException.class, PhoneValidationException.class})
    public ResponseEntity<ProblemDetail> handleCustomerValidationException(Exception ex) {
        log.error("Customer validation failed: {}", ex.getMessage());

        var problem = handleException(
                ex,
                HttpStatus.BAD_REQUEST,
                "Customer Validation Failed",
                "customers/validation-error",
                exception -> {
                    if (exception instanceof CustomerValidationException customerEx) {
                        return Map.of("validationErrors", customerEx.getViolations());
                    } else if (exception instanceof PhoneValidationException phoneEx) {
                        return Map.of("validationErrors", phoneEx.getViolations());
                    }
                    return Map.of();
                }
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(problem);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ProblemDetail> handleHttpMessageNotReadable(HttpMessageNotReadableException ex) {
        // Check if the root cause is a PhoneValidationException
        Throwable cause = ex.getCause();
        while (cause != null) {
            if (cause instanceof PhoneValidationException phoneEx) {
                return handleCustomerValidationException(phoneEx);
            }
            cause = cause.getCause();
        }

        // Handle other JSON parsing errors
        var problem = handleException(
                ex,
                HttpStatus.BAD_REQUEST,
                "Invalid Request Format",
                "customers/format-error",
                exception -> Map.of("error", "The request contains invalid data")
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(problem);
    }



    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ProblemDetail> handleMethodArgumentNotValid(MethodArgumentNotValidException ex) {
        return handleValidationException(
                ex,
                "Customer Request Validation Failed",
                "Customer request contains invalid data",
                "customers/validation-error"
        );
    }
}