package com.bynature.adapters.out.persistence.jpa;

import com.bynature.adapters.out.persistence.jpa.entity.OrderEntity;
import com.bynature.adapters.out.persistence.jpa.entity.ShippingAddressEntity;
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

    // Example mapping method: adjust as needed to suit your mapping requirements.
    private OrderEntity mapToEntity(Order order) {
        // Create OrderEntity from Order domain object.
        // For example, mapping ShippingAddress (assuming OrderEntity has an AddressEntity field).
        OrderEntity entity = new OrderEntity();
        entity.setId(order.getId());
        entity.setCustomerId(order.getCustomerId());
        entity.setTotal(order.getTotal());
        entity.setStatus(order.getStatus());

        // Mapping ShippingAddress (you should implement the Address mapping accordingly)
        if (order.getShippingAddress() != null) {
            // Assume AddressEntity has a matching constructor or setters.
            entity.setShippingAddress(new ShippingAddressEntity(
                    UUID.randomUUID(),
                    order.getShippingAddress().getFirstName(),
                    order.getShippingAddress().getLastName(),
                    order.getShippingAddress().getPhoneNumber().number(),
                    order.getShippingAddress().getEmail().email(),
                    order.getShippingAddress().getStreetNumber(),
                    order.getShippingAddress().getStreet(),
                    order.getShippingAddress().getCity(),
                    order.getShippingAddress().getRegion(),
                    order.getShippingAddress().getPostalCode(),
                    order.getShippingAddress().getCountry()));
        }
        // Map order items if needed.
        return entity;
    }

    private Order mapToDomain(OrderEntity entity) {
        return entity.toDomain();
    }
}
