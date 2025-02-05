package com.bynature.adapters.out.persistence;

import com.bynature.adapters.out.persistence.entity.ItemEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface OrderJpaRepository extends JpaRepository<ItemEntity, UUID> {

}
