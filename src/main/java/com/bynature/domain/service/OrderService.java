package com.bynature.domain.service;

import com.bynature.domain.model.Order;

import java.util.UUID;

public interface OrderService {

    UUID createOrder(Order order) ;

    void updateOrder(Order order);

    Order getOrder(UUID orderId) ;

    void deleteOrder(UUID orderId) ;
}
