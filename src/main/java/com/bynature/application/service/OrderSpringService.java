package com.bynature.application.service;

import com.bynature.domain.model.Order;
import com.bynature.domain.model.OrderStatus;
import com.bynature.domain.repository.OrderRepository;
import com.bynature.domain.service.OrderService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
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
        order.setUpdatedAt(LocalDateTime.now());
        orderRepository.updateOrder(order);
    }

    public void updateOrderStatus(UUID orderId, OrderStatus status) {
        orderRepository.updateOrderStatus(orderId, status);
    }

    public void updateOrderStatus(UUID orderId, OrderStatus status, String paymentIntentId) {
        orderRepository.updateOrderStatus(orderId, status, paymentIntentId);
    }

    public Order getOrder(UUID orderId) {
        return orderRepository.getOrder(orderId);
    }

    @Override
    public List<Order> getOrdersByCustomer(UUID customerId) {
        return orderRepository.getOrdersByCustomer(customerId);
    }

    public void deleteOrder(UUID orderId) {
        orderRepository.deleteOrder(orderId);
    }
}
