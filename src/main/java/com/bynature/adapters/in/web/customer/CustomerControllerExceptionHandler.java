package com.bynature.adapters.in.web.customer;

import com.bynature.adapters.in.web.exception.BaseExceptionHandler;
import com.bynature.domain.exception.CustomerNotFoundException;
import com.bynature.domain.exception.CustomerValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
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

    @ExceptionHandler(CustomerValidationException.class)
    public ResponseEntity<ProblemDetail> handleCustomerValidationException(CustomerValidationException ex) {
        log.error("Customer validation failed: {}", ex.getMessage());

        var problem = handleException(
                ex,
                HttpStatus.BAD_REQUEST,
                "Customer Validation Failed",
                "customers/validation-error",
                exception -> Map.of("validationErrors", ex.getViolations())
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