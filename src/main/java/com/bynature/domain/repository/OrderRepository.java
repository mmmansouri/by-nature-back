package com.bynature.domain.repository;

import com.bynature.domain.model.Order;

import java.util.UUID;

public interface OrderRepository {

    UUID saveOrder(Order order);

    void updateOrder(Order order);

    Order getOrder(UUID orderId);

    void deleteOrder(UUID orderId);
}
