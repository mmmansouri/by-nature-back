package com.bynature.domain.exception;

import java.util.UUID;

public class OrderNotFoundException extends RuntimeException {

    private final UUID orderId;

    /**
     * Constructs a new exception with the specified detail message and order ID.
     *
     * @param message the detail message
     * @param orderId the ID of the order that was not found
     */
    public OrderNotFoundException(String message, UUID orderId) {
        super(message);
        this.orderId = orderId;
    }

    /**
     * Constructs a new exception with a standard message for the given order ID.
     *
     * @param orderId the ID of the order that was not found
     */
    public OrderNotFoundException(UUID orderId) {
        super("Order not found with ID: " + orderId);
        this.orderId = orderId;
    }

    /**
     * Constructs a new exception with the specified detail message.
     *
     * @param message the detail message
     */
    public OrderNotFoundException(String message) {
        super(message);
        this.orderId = null;
    }

    /**
     * Returns the ID of the order that was not found.
     *
     * @return the order ID, or null if not available
     */
    public UUID getOrderId() {
        return orderId;
    }
}