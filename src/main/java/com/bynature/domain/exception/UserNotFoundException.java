package com.bynature.domain.exception;

import java.util.UUID;

public class UserNotFoundException extends RuntimeException {

    private final UUID userId;

    /**
     * Constructs a new exception with the specified detail message and customer ID.
     *
     * @param message the detail message
     * @param userId the ID of the customer that was not found
     */
    public UserNotFoundException(String message, UUID userId) {
        super(message);
        this.userId = userId;
    }

    /**
     * Constructs a new exception with a standard message for the given customer ID.
     *
     * @param userId the ID of the customer that was not found
     */
    public UserNotFoundException(UUID userId) {
        super("Customer not found with ID: " + userId);
        this.userId = userId;
    }

    /**
     * Constructs a new exception with the specified detail message.
     *
     * @param message the detail message
     */
    public UserNotFoundException(String message) {
        super(message);
        this.userId = null;
    }

    /**
     * Returns the ID of the customer that was not found.
     *
     * @return the customer ID, or null if not available
     */
    public UUID getUserId() {
        return userId;
    }
}