package com.bynature.adapters.out.persistence.jpa.repository;

import com.bynature.adapters.out.persistence.jpa.entity.OrderEntity;
import com.bynature.domain.model.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.UUID;

public interface OrderJpaRepository extends JpaRepository<OrderEntity, UUID>  {

    @Modifying
    @Query("UPDATE OrderEntity o SET o.paymentIntentId = :paymentIntentId, o.status = :status," +
            " o.updatedAt = :updatedAt WHERE o.id = :orderId")
    void updateOrderStatus(UUID orderId,  OrderStatus status, String paymentIntentId,
                                           LocalDateTime updatedAt);

    @Modifying
    @Query("UPDATE OrderEntity o SET o.status = :status, o.updatedAt = :updatedAt WHERE o.id = :orderId")
    void updateOrderStatus(UUID orderId, OrderStatus status, LocalDateTime updatedAt);
}
