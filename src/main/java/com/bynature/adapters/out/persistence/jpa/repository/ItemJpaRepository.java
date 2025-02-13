package com.bynature.adapters.out.persistence.jpa.repository;

import com.bynature.adapters.out.persistence.jpa.entity.ItemEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;


public interface ItemJpaRepository  extends JpaRepository<ItemEntity, UUID>{
}
