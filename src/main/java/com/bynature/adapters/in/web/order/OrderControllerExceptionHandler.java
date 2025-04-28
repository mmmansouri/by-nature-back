package com.bynature.adapters.in.web.order;

import com.bynature.adapters.in.web.exception.BaseExceptionHandler;
import com.bynature.domain.exception.OrderNotFoundException;
import com.bynature.domain.exception.OrderValidationException;
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

@RestControllerAdvice(assignableTypes = {OrderController.class})
@Order(1) // High priority, same as ItemControllerExceptionHandler
public class OrderControllerExceptionHandler extends BaseExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(OrderControllerExceptionHandler.class);
    
    // Register managed exception types in static initializer
    static {
        registerHandledExceptionType(OrderNotFoundException.class);
        registerHandledExceptionType(OrderValidationException.class);
    }

    @ExceptionHandler(OrderNotFoundException.class)
    public ResponseEntity<ProblemDetail> handleOrderNotFound(OrderNotFoundException ex) {
        log.error("Order not found: {}", ex.getMessage());

        var problem = handleException(
                ex,
                HttpStatus.NOT_FOUND,
                "Order Not Found",
                "orders/not-found",
                exception -> ex.getOrderId() != null
                        ? Map.of("orderId", ex.getOrderId())
                        : Map.of()
        );

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(problem);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ProblemDetail> handleOrderValidationException(MethodArgumentNotValidException ex) {
        return handleValidationException(
                ex,
                "Order Creation Request Validation Failed",
                "Order creation request contains invalid data",
                "orders/validation-error"
        );
    }
}
