package com.bynature.domain.exception;

import java.util.UUID;

public class CustomerNotFoundException extends RuntimeException {

    private final UUID customerId;

    /**
     * Constructs a new exception with the specified detail message and customer ID.
     *
     * @param message the detail message
     * @param customerId the ID of the customer that was not found
     */
    public CustomerNotFoundException(String message, UUID customerId) {
        super(message);
        this.customerId = customerId;
    }

    /**
     * Constructs a new exception with a standard message for the given customer ID.
     *
     * @param customerId the ID of the customer that was not found
     */
    public CustomerNotFoundException(UUID customerId) {
        super("Customer not found with ID: " + customerId);
        this.customerId = customerId;
    }

    /**
     * Constructs a new exception with the specified detail message.
     *
     * @param message the detail message
     */
    public CustomerNotFoundException(String message) {
        super(message);
        this.customerId = null;
    }

    /**
     * Returns the ID of the customer that was not found.
     *
     * @return the customer ID, or null if not available
     */
    public UUID getCustomerId() {
        return customerId;
    }
}