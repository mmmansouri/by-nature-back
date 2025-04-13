package com.bynature.domain.exception;

import java.util.UUID;

public class ItemNotFoundException extends RuntimeException {

    private final UUID itemId;

    /**
     * Constructs a new exception with the specified detail message and item ID.
     *
     * @param message the detail message
     * @param itemId the ID of the item that was not found
     */
    public ItemNotFoundException(String message, UUID itemId) {
        super(message);
        this.itemId = itemId;
    }

    /**
     * Constructs a new exception with a standard message for the given item ID.
     *
     * @param itemId the ID of the item that was not found
     */
    public ItemNotFoundException(UUID itemId) {
        super("Item not found with ID: " + itemId);
        this.itemId = itemId;
    }

    /**
     * Constructs a new exception with the specified detail message.
     *
     * @param message the detail message
     */
    public ItemNotFoundException(String message) {
        super(message);
        this.itemId = null;
    }

    /**
     * Returns the ID of the item that was not found.
     *
     * @return the item ID, or null if not available
     */
    public UUID getItemId() {
        return itemId;
    }
}