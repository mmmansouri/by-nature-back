package com.bynature.adapters.out.persistence.jpa.repository;

import com.bynature.adapters.out.persistence.jpa.entity.OrderEntity;
import com.bynature.domain.model.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface OrderJpaRepository extends JpaRepository<OrderEntity, UUID>  {

    @Modifying
    @Query("UPDATE OrderEntity o SET o.paymentIntentId = :paymentIntentId, o.status = :status," +
            " o.updatedAt = :updatedAt WHERE o.id = :orderId")
    void updateOrderStatus(UUID orderId,  OrderStatus status, String paymentIntentId,
                                           LocalDateTime updatedAt);

    @Modifying
    @Query("UPDATE OrderEntity o SET o.status = :status, o.updatedAt = :updatedAt WHERE o.id = :orderId")
    void updateOrderStatus(UUID orderId, OrderStatus status, LocalDateTime updatedAt);

    // Fix naming: use customer.id to match entity relationship structure
    @Query("SELECT o FROM OrderEntity o WHERE o.customer.id = :customerId")
    List<OrderEntity> findByCustomerId(UUID customerId);

    // Async version using virtual threads (Java 21+)
    @Query("SELECT o FROM OrderEntity o WHERE o.customer.id = :customerId")
    CompletableFuture<List<OrderEntity>> findByCustomerIdAsync(UUID customerId);

    // Paginated version for large result sets
    Page<OrderEntity> findByCustomer_Id(UUID customerId, Pageable pageable);

    // Filter by customer and status
    List<OrderEntity> findByCustomer_IdAndStatus(UUID customerId, OrderStatus status);
}

