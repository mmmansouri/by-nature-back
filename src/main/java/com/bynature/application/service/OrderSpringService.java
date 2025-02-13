package com.bynature.application.service;

import com.bynature.domain.model.Order;
import com.bynature.domain.repository.OrderRepository;
import com.bynature.domain.service.OrderService;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class OrderSpringService implements OrderService {

    private final OrderRepository orderRepository;

    public OrderSpringService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    public UUID createOrder(Order order) {
        return orderRepository.saveOrder(order);
    }

    public void updateOrder(Order order) {
        orderRepository.updateOrder(order);
    }

    public Order getOrder(UUID orderId) {
        return orderRepository.getOrder(orderId);
    }

    public void deleteOrder(UUID orderId) {
        orderRepository.deleteOrder(orderId);
    }
}
