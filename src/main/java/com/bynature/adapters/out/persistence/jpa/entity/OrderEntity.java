package com.bynature.adapters.out.persistence.jpa.entity;


import com.bynature.domain.model.Order;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Entity
@Table(name = "orders")
public class OrderEntity {
    @Id
    private UUID id;

    @Column(name = "customer_id", nullable = false)
    private UUID customerId;

    @Column(nullable = false)
    private double total;

    @Column(nullable = false)
    private String status;

    // Assuming ShippingAddress is a value object, map it as an embeddable.
    @Embedded
    private ShippingAddressEntity shippingAddress;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItemEntity> orderItems = new ArrayList<>();

    // Constructors
    public OrderEntity() {
    }

    public OrderEntity(UUID id, UUID customerId, double total, String status, ShippingAddressEntity shippingAddress) {
        this.id = id;
        this.customerId = customerId;
        this.total = total;
        this.status = status;
        this.shippingAddress = shippingAddress;
    }

    // Getters and setters

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getCustomerId() {
        return customerId;
    }

    public void setCustomerId(UUID customerId) {
        this.customerId = customerId;
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

    public ShippingAddressEntity getShippingAddress() {
        return shippingAddress;
    }

    public void setShippingAddress(ShippingAddressEntity shippingAddress) {
        this.shippingAddress = shippingAddress;
    }

    public List<OrderItemEntity> getOrderItems() {
        return orderItems;
    }

    public void setOrderItems(List<OrderItemEntity> orderItems) {
        this.orderItems = orderItems;
    }

    // Helper to add order items
    public void addOrderItem(OrderItemEntity orderItem) {
        orderItems.add(orderItem);
        orderItem.setOrder(this);
    }

    // Helper to remove order items
    public void removeOrderItem(OrderItemEntity orderItem) {
        orderItems.remove(orderItem);
        orderItem.setOrder(null);
    }

    public Order toDomain() {
        return new Order(
                this.id,
                this.customerId,
                this.orderItems.stream()
                        .collect(Collectors.toMap(orderItem -> orderItem.getItem().getId(), OrderItemEntity::getQuantity)),
                this.total,
                this.status,
                this.shippingAddress.toDomain()
        );
    }

}
