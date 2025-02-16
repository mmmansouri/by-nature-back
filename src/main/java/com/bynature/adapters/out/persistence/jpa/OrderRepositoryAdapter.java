package com.bynature.adapters.out.persistence.jpa;

import com.bynature.adapters.out.persistence.jpa.entity.OrderEntity;
import com.bynature.adapters.out.persistence.jpa.repository.OrderJpaRepository;
import com.bynature.domain.model.Order;
import com.bynature.domain.repository.OrderRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public class OrderRepositoryAdapter implements OrderRepository {

    private final OrderJpaRepository orderJpaRepository;

    public OrderRepositoryAdapter(OrderJpaRepository orderJpaRepository) {
        this.orderJpaRepository = orderJpaRepository;
    }

    @Override
    public UUID saveOrder(Order order) {
        // Map domain Order to OrderEntity (implement mapping as needed)
        OrderEntity entity = mapToEntity(order);
        OrderEntity savedEntity = orderJpaRepository.save(entity);
        return savedEntity.getId();
    }

    @Override
    public void updateOrder(Order order) {
        // Assuming the order already exists; you can implement update logic accordingly
        OrderEntity entity = mapToEntity(order);
        orderJpaRepository.save(entity);
    }

    @Override
    public Order getOrder(UUID orderId) {
        Optional<OrderEntity> optionalEntity = orderJpaRepository.findById(orderId);
        return optionalEntity.map(this::mapToDomain).orElse(null);
    }

    @Override
    public void deleteOrder(UUID orderId) {
        orderJpaRepository.deleteById(orderId);
    }

    private OrderEntity mapToEntity(Order order) {
        //TODO: Map order items
        //entity.setOrderItems(order.getOrderItems().);

        return new OrderEntity(order.getId(), order.getCustomerId(), order.getTotal(), order.getStatus(),
                order.getFirstName(), order.getLastName(), order.getPhoneNumber().number(), order.getEmail().email(),
                order.getStreetNumber(), order.getStreet(), order.getCity(), order.getRegion(), order.getPostalCode(),
                order.getCountry(), order.getCreatedAt(), order.getUpdatedAt());
    }

    private Order mapToDomain(OrderEntity entity) {
        return entity.toDomain();
    }
}
