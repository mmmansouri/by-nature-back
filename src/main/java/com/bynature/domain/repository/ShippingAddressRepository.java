package com.bynature.domain.repository;

import com.bynature.domain.model.ShippingAddress;

import java.util.List;
import java.util.UUID;

public interface ShippingAddressRepository {
    UUID saveShippingAddress(ShippingAddress shippingAddress);

    void updateShippingAddress(ShippingAddress shippingAddress);

    ShippingAddress getShippingAddress(UUID id);

    List<ShippingAddress> getShippingAddressesByCustomer(UUID customerId);

    void deleteShippingAddress(UUID id);
}