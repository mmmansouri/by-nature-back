package com.bynature.domain.service;

import com.bynature.domain.model.Order;
import com.bynature.domain.repository.OrderRepository;

import java.util.UUID;

public class OrderService {

    private OrderRepository orderRepository;

    UUID createOrder(Order order) {
        return orderRepository.saveOrder(order);
    }

    void updateOrder(Order order) {
        orderRepository.updateOrder(order);
    }

    Order getOrder(UUID orderId) {
        return orderRepository.getOrder(orderId);
    }

    void deleteOrder(UUID orderId) {
        orderRepository.deleteOrder(orderId);
    }
}
