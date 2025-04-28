package com.bynature.adapters.in.web.order.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.util.UUID;

public record OrderItemCreationRequest(@NotNull(message = "Item ID is required")UUID itemId,
                                       @Positive(message = "Quantity must be positive")int quantity) {

    public static OrderItemCreationRequest fromDomain(UUID itemId, int quantity) {
        return new OrderItemCreationRequest(itemId, quantity);
    }
}
