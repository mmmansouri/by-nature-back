package com.bynature.adapters.out.persistence.jpa.repository;

import com.bynature.adapters.out.persistence.jpa.entity.ShippingAddressEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface ShippingAddressJpaRepository extends JpaRepository<ShippingAddressEntity, UUID> {

    @Query("SELECT sa FROM ShippingAddressEntity sa WHERE sa.customer.id = :customerId")
    List<ShippingAddressEntity> findByCustomerId(UUID customerId);

    @Query("SELECT sa FROM ShippingAddressEntity sa WHERE sa.customer.id = :customerId")
    CompletableFuture<List<ShippingAddressEntity>> findByCustomerIdAsync(UUID customerId);

    Page<ShippingAddressEntity> findByCustomer_Id(UUID customerId, Pageable pageable);
}