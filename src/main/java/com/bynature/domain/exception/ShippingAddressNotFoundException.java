package com.bynature.domain.exception;

import java.util.UUID;

public class ShippingAddressNotFoundException extends RuntimeException {
    private final UUID shippingAddressId;

    public ShippingAddressNotFoundException(String message, UUID shippingAddressId) {
        super(message);
        this.shippingAddressId = shippingAddressId;
    }

    public UUID getShippingAddressId() {
        return shippingAddressId;
    }
}