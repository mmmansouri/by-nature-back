package com.bynature.domain.repository;

import com.bynature.domain.model.Order;
import com.bynature.domain.model.OrderStatus;

import java.util.List;
import java.util.UUID;

public interface OrderRepository {

    UUID saveOrder(Order order);

    void updateOrder(Order order);

    Order getOrder(UUID orderId);

    void deleteOrder(UUID orderId);

    void updateOrderStatus(UUID orderId, OrderStatus status);

    void updateOrderStatus(UUID orderId, OrderStatus status, String paymentIntentId);

    List<Order> getOrdersByCustomer(UUID customerId);
}
