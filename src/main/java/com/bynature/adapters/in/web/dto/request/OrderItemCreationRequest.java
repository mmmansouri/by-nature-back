package com.bynature.adapters.in.web.dto.request;

import java.util.UUID;

public record OrderItemCreationRequest(UUID itemId, int quantity) {

    public static OrderItemCreationRequest fromDomain(UUID itemId, int quantity) {
        return new OrderItemCreationRequest(itemId, quantity);
    }
}
