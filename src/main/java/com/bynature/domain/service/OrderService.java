package com.bynature.domain.service;

import com.bynature.domain.model.Order;
import com.bynature.domain.model.OrderStatus;

import java.util.List;
import java.util.UUID;

public interface OrderService {

    UUID createOrder(Order order);

    void updateOrder(Order order);

    Order getOrder(UUID orderId);

    List<Order> getOrdersByCustomer(UUID customerId);

    void deleteOrder(UUID orderId);

    void updateOrderStatus(UUID orderId, OrderStatus status);

    void updateOrderStatus(UUID orderId,OrderStatus status, String paymentIntentId);
}
