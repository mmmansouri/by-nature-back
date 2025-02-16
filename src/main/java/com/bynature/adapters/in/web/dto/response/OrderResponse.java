package com.bynature.adapters.in.web.dto.response;


import com.bynature.domain.model.Order;

import java.util.Map;
import java.util.UUID;

public class OrderResponse {
    private final UUID id;
    private final UUID customerId;
    private final Map<UUID, Integer> orderItems;
    private final double total;
    private final String status;
    private final ShippingAddressResponse shippingAddress;

    public OrderResponse(UUID id, UUID customerId, Map<UUID, Integer> orderItems, double total, String status, ShippingAddressResponse shippingAddress) {
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

    public ShippingAddressResponse getShippingAddress() {
        return shippingAddress;
    }

    public static OrderResponse fromDomain(Order order) {
        return new OrderResponse(order.getId(), order.getCustomerId(), order.getOrderItems(), order.getTotal(), order.getStatus(), ShippingAddressResponse.fromDomain(order.getShippingAddress()));
    }
}
