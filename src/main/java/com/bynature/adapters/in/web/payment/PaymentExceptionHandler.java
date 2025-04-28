package com.bynature.adapters.in.web.payment;

import com.bynature.adapters.in.web.exception.BaseExceptionHandler;
import com.stripe.exception.StripeException;
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

@RestControllerAdvice(assignableTypes = {PaymentIntentController.class, StripeWebhookController.class})
@Order(1) // Higher priority than GlobalExceptionHandler
public class PaymentExceptionHandler extends BaseExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(PaymentExceptionHandler.class);

    // Register managed exception types in static initializer
    static {
        registerHandledExceptionType(StripeException.class);
    }

    @ExceptionHandler(StripeException.class)
    public ResponseEntity<ProblemDetail> handleStripeException(StripeException ex) {
        log.error("Stripe payment error: {}", ex.getMessage());

        var problemDetail = handleException(
                ex,
                HttpStatus.BAD_REQUEST,
                "Payment Processing Error",
                "payment",
                exception -> Map.ofEntries(
                        Map.entry("stripeCode", exception.getCode() != null ? exception.getCode() : "unknown"),
                        Map.entry("stripeStatusCode", exception.getStatusCode()),
                        Map.entry("stripeType", exception.getStripeError() != null ?
                                exception.getStripeError().getType() : "unknown")
                )
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(problemDetail);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ProblemDetail> handlePaymentValidationException(MethodArgumentNotValidException ex) {
        return handleValidationException(
                ex,
                "Payment Request Validation Failed",
                "The payment request contains invalid data",
                "payment/validation-error"
        );
    }
}
