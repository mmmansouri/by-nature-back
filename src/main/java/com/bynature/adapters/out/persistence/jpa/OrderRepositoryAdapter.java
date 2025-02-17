package com.bynature.adapters.out.persistence.jpa;

import com.bynature.adapters.out.persistence.jpa.entity.ItemEntity;
import com.bynature.adapters.out.persistence.jpa.entity.OrderEntity;
import com.bynature.adapters.out.persistence.jpa.entity.OrderItemEntity;
import com.bynature.adapters.out.persistence.jpa.entity.OrderItemId;
import com.bynature.adapters.out.persistence.jpa.repository.ItemJpaRepository;
import com.bynature.adapters.out.persistence.jpa.repository.OrderJpaRepository;
import com.bynature.domain.model.Order;
import com.bynature.domain.repository.OrderRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Repository
public class OrderRepositoryAdapter implements OrderRepository {

    private final OrderJpaRepository orderJpaRepository;
    private final ItemJpaRepository itemJpaRepository;

    public OrderRepositoryAdapter(OrderJpaRepository orderJpaRepository, ItemJpaRepository itemJpaRepository) {
        this.orderJpaRepository = orderJpaRepository;
        this.itemJpaRepository = itemJpaRepository;
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

        OrderEntity orderEntity = new OrderEntity(order.getId(), order.getCustomerId(), order.getTotal(), order.getStatus(),
                order.getFirstName(), order.getLastName(), order.getPhoneNumber().number(), order.getEmail().email(),
                order.getStreetNumber(), order.getStreet(), order.getCity(), order.getRegion(), order.getPostalCode(),
                order.getCountry(), order.getCreatedAt(), order.getUpdatedAt());

        // Retrieve all item IDs from the order's items.
        Set<UUID> itemIds = order.getOrderItems().keySet();

        // Use a single query to fetch all items.
        List<ItemEntity> itemEntities = itemJpaRepository.findAllById(itemIds);

        // Create a map for fast lookup by ID.
        Map<UUID, ItemEntity> itemEntityMap = itemEntities.stream()
                .collect(Collectors.toMap(ItemEntity::getId, Function.identity()));

        // Iterate over each order item and build the OrderItemEntity objects.
        order.getOrderItems().forEach((itemId, quantity) -> {
            ItemEntity itemEntity = itemEntityMap.get(itemId);
            if (itemEntity == null) {
                throw new RuntimeException("Item not found for id: " + itemId);
            }
            OrderItemEntity orderItemEntity = new OrderItemEntity();
            orderItemEntity.setId(new OrderItemId(order.getId(), itemEntity.getId()));
            orderItemEntity.setOrder(orderEntity);
            orderItemEntity.setItem(itemEntity);
            orderItemEntity.setQuantity(quantity);

            // Add the OrderItemEntity to the OrderEntity's list.
            orderEntity.addOrderItem(orderItemEntity);
        });

        return orderEntity;
    }

    private Order mapToDomain(OrderEntity entity) {
        return entity.toDomain();
    }
}
