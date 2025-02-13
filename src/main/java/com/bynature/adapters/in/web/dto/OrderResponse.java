package com.bynature.adapters.in.web.dto;

import java.util.UUID;

public class OrderResponse {
    private UUID id;

    public OrderResponse(UUID id) {
        this.id = id;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }
}
