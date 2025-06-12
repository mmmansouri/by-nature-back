package com.bynature.adapters.out.persistence.jpa.adapter;

import com.bynature.adapters.out.persistence.jpa.adapter.mapper.EntityMapper;
import com.bynature.adapters.out.persistence.jpa.entity.CustomerEntity;
import com.bynature.adapters.out.persistence.jpa.entity.ItemEntity;
import com.bynature.adapters.out.persistence.jpa.entity.OrderEntity;
import com.bynature.adapters.out.persistence.jpa.entity.OrderItemEntity;
import com.bynature.adapters.out.persistence.jpa.entity.OrderItemId;
import com.bynature.adapters.out.persistence.jpa.repository.ItemJpaRepository;
import com.bynature.adapters.out.persistence.jpa.repository.OrderJpaRepository;
import com.bynature.domain.exception.OrderNotFoundException;
import com.bynature.domain.model.Order;
import com.bynature.domain.model.OrderStatus;
import com.bynature.domain.repository.OrderRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.stream.Collectors;

@Repository
public class OrderRepositoryAdapter implements OrderRepository {

    private static final Logger log = LoggerFactory.getLogger(OrderRepositoryAdapter.class);

    private final OrderJpaRepository orderJpaRepository;
    private final ItemJpaRepository itemJpaRepository;

    public OrderRepositoryAdapter(OrderJpaRepository orderJpaRepository, ItemJpaRepository itemJpaRepository) {
        this.orderJpaRepository = orderJpaRepository;
        this.itemJpaRepository = itemJpaRepository;
    }

    @Override
    @Transactional
    public UUID saveOrder(Order order) {
        log.debug("Saving order with ID: {} for status {}", order.getId(), order.getStatus());

        OrderEntity entity = mapToEntity(order);
        OrderEntity savedEntity = orderJpaRepository.save(entity);

        log.info("Order saved with ID: {} for status {}", order.getId(), order.getStatus());

        return savedEntity.getId();
    }

    @Override
    @Transactional
    public void updateOrder(Order order) {
        log.debug("Updating order with ID: {} for status {}", order.getId(), order.getStatus());

        // Verify order exists before updating
        getOrder(order.getId());

        OrderEntity entity = mapToEntity(order);
        orderJpaRepository.save(entity);

        log.info("Order updated with ID: {} for status {}", order.getId(), order.getStatus());
    }

    @Override
    @Transactional
    public void updateOrderStatus(UUID orderId,  OrderStatus status, String paymentIntentId) {
        log.debug("Updating order status with ID: {} for status {}", orderId, status);

        // Verify order exists before updating
        getOrder(orderId);

        orderJpaRepository.updateOrderStatus(orderId, status, paymentIntentId, LocalDateTime.now());

        log.info("Order status updated with ID: {} for status {} and paymentIntentId {}", orderId,status,paymentIntentId);
    }

    @Override
    public List<Order> getOrdersByCustomer(UUID customerId) {
        return orderJpaRepository.findByCustomerId(customerId)
                .stream()
                .map(EntityMapper::mapOrderToDomain)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void updateOrderStatus(UUID orderId, OrderStatus status) {
        log.debug("Updating order status with ID: {} for status {}", orderId, status);
        orderJpaRepository.updateOrderStatus(orderId, status, LocalDateTime.now());
        log.info("Order status updated with ID: {} for status {}", orderId,status);
    }

    @Override
    @Transactional(readOnly = true)
    public Order getOrder(UUID orderId) {

        log.debug("Fetching order with ID: {}", orderId);

        return orderJpaRepository.findById(orderId)
                .map(EntityMapper::mapOrderToDomain)
                .orElseThrow(() -> new OrderNotFoundException("Order not found with id: " + orderId, orderId));
    }

    @Override
    @Transactional
    public void deleteOrder(UUID orderId) {

        log.debug("Deleting order with ID: {}", orderId);

        // Verify customer exists before deleting
        getOrder(orderId);
        orderJpaRepository.deleteById(orderId);

        log.info("Customer deleted with ID: {}", orderId);
    }



    private OrderEntity mapToEntity(Order order) {

        OrderEntity orderEntity = new OrderEntity(order.getId(), CustomerEntity.fromDomain(order.getCustomer()), order.getTotal(), order.getStatus(),
                order.getFirstName(), order.getLastName(), order.getPhoneNumber().number(), order.getEmail().email(),
                order.getStreetNumber(), order.getStreet(), order.getCity(), order.getRegion(), order.getPostalCode(),
                order.getCountry(), order.getCreatedAt(), order.getUpdatedAt());

        // Retrieve all item IDs from the order's items.
        Set<UUID> itemIds = order.getOrderItems().stream()
                .map(model -> model.getItem().getId()).collect(Collectors.toSet());

        // Use a single query to fetch all items.
        List<ItemEntity> itemEntities = itemJpaRepository.findAllById(itemIds);

        // Create a map for fast lookup by ID.
        Map<UUID, ItemEntity> itemEntityMap = itemEntities.stream()
                .collect(Collectors.toMap(ItemEntity::getId, Function.identity()));

        // Iterate over each order item and build the OrderItemEntity objects.
        order.getOrderItems().forEach(orderItem -> {
            ItemEntity itemEntity = itemEntityMap.get(orderItem.getItem().getId());
            if (itemEntity == null) {
                throw new RuntimeException("Item not found for id: " + orderItem.getItem().getId());
            }
            OrderItemEntity orderItemEntity = new OrderItemEntity();
            orderItemEntity.setId(new OrderItemId(order.getId(), itemEntity.getId()));
            orderItemEntity.setOrder(orderEntity);
            orderItemEntity.setItem(itemEntity);
            orderItemEntity.setQuantity(orderItem.getQuantity());

            // Add the OrderItemEntity to the OrderEntity's list.
            orderEntity.addOrderItem(orderItemEntity);
        });

        return orderEntity;
    }

    public CompletableFuture<List<Order>> getOrdersByCustomerIdAsync(UUID customerId) {
        return orderJpaRepository.findByCustomerIdAsync(customerId)
                .thenApply(orders -> orders.stream()
                        .map(EntityMapper::mapOrderToDomain)
                        .collect(Collectors.toList()));
    }

    public Page<Order> getOrdersByCustomerIdPaginated(UUID customerId, PageRequest pageRequest) {
        Page<OrderEntity> orderPage = orderJpaRepository.findByCustomer_Id(customerId, pageRequest);
        return orderPage.map(EntityMapper::mapOrderToDomain);
    }

    public List<Order> getOrdersByCustomerAndStatus(UUID customerId, OrderStatus orderStatus) {
        List<OrderEntity> orderEntities = orderJpaRepository.findByCustomer_IdAndStatus(customerId, orderStatus);
        return orderEntities.stream()
                .map(EntityMapper::mapOrderToDomain)
                .collect(Collectors.toList());
    }

}
