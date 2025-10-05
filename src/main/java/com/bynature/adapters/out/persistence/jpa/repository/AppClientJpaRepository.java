package com.bynature.adapters.out.persistence.jpa.repository;

import com.bynature.adapters.out.persistence.jpa.entity.AppClientEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface AppClientJpaRepository extends JpaRepository<AppClientEntity, UUID> {
    List<AppClientEntity> findByAppClientId(String appClientId);
}
