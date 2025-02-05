package com.bynature.adapters.out.persistence;

import com.bynature.adapters.out.persistence.entity.OrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface OrderJpaAdapter extends JpaRepository<OrderEntity, UUID> {
}
