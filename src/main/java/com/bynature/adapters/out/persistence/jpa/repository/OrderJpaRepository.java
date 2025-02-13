package com.bynature.adapters.out.persistence.jpa.repository;

import com.bynature.adapters.out.persistence.jpa.entity.OrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface OrderJpaRepository extends JpaRepository<OrderEntity, UUID>  {

}
