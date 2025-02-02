package com.bynature.domain.model;

import java.util.Map;
import java.util.UUID;

public class Order {
    private UUID id;
    private UUID customerId;
    private Map<UUID, Integer> orderItems;
    private double total;
    private String status;
    private ShippingAddress shippingAddress;

    public Order(UUID id, UUID customerId, Map<UUID, Integer> orderItems, double total, String status, ShippingAddress shippingAddress) {
        this.id = id;
        this.customerId = customerId;
        this.orderItems = orderItems;
        this.total = total;
        this.status = status;
        this.shippingAddress = shippingAddress;
    }

    public UUID getId() {
        return id;
    }

    public UUID getCustomerId() {
        return customerId;
    }

    public Map<UUID, Integer> getOrderItems() {
        return orderItems;
    }

    public double getTotal() {
        return total;
    }

    public String getStatus() {
        return status;
    }

    public ShippingAddress getShippingAddress() {
        return shippingAddress;
    }
}
