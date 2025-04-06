package com.bynature.domain.model;

public enum OrderStatus {
    CREATED,
    PAYMENT_INTEND_CREATED,
    PAYMENT_PROCESSING,
    PAYMENT_CONFIRMED,
    PAYMENT_FAILED,
    SHIPPED,
    DELIVERED,
    CANCELLED
}
