package com.bynature.adapters.in.web.dto.request;

import com.bynature.domain.model.Email;
import com.bynature.domain.model.Order;
import com.bynature.domain.model.PhoneNumber;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

public class OrderRequest {
    private UUID customerId;
    private Map<UUID, Integer> orderItems;
    private double total;
    private String status;
    private ShippingAddressRequest shippingAddress;

    // Getters and setters

    public UUID getCustomerId() {
        return customerId;
    }

    public void setCustomerId(UUID customerId) {
        this.customerId = customerId;
    }

    public Map<UUID, Integer> getOrderItems() {
        return orderItems;
    }

    public void setOrderItems(Map<UUID, Integer> orderItems) {
        this.orderItems = orderItems;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public ShippingAddressRequest getShippingAddress() {
        return shippingAddress;
    }

    public void setShippingAddress(ShippingAddressRequest shippingAddress) {
        this.shippingAddress = shippingAddress;
    }

    public Order toDomain() {
        return new Order(
                UUID.randomUUID(), // generate ID here or within the use case
                this.customerId,
                this.orderItems,
                this.total,
                this.status,
                this.shippingAddress.getFirstName(),
                this.shippingAddress.getLastName(),
                new PhoneNumber(this.shippingAddress.getPhoneNumber()),
                new Email(this.shippingAddress.getEmail()),
                this.shippingAddress.getStreetNumber(),
                this.shippingAddress.getStreet(),
                this.shippingAddress.getCity(),
                this.shippingAddress.getRegion(),
                this.shippingAddress.getPostalCode(),
                this.shippingAddress.getCountry(),
                LocalDateTime.now(),
                LocalDateTime.now());
    }
}
