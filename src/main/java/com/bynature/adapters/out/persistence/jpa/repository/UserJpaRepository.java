package com.bynature.adapters.out.persistence.jpa.repository;

import com.bynature.adapters.out.persistence.jpa.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

public interface UserJpaRepository extends JpaRepository<UserEntity, UUID> {
    Optional<UserEntity> findByEmail(String email);

    @Modifying
    @Query("UPDATE UserEntity u SET u.active = :active, u.updatedAt = :updatedAt WHERE u.id = :userId")
    void updateUserActiveStatus(UUID userId, boolean active, LocalDateTime updatedAt);

    @Modifying
    @Query("UPDATE UserEntity u SET u.lastLoginAt = :lastLoginAt, u.updatedAt = :updatedAt WHERE u.id = :userId")
    void updateUserLastLogin(UUID userId, LocalDateTime lastLoginAt, LocalDateTime updatedAt);

    Optional<UserEntity> findByCustomer_Id(UUID customerId);
}