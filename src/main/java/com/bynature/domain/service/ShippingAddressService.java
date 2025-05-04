package com.bynature.domain.service;

import com.bynature.domain.model.ShippingAddress;

import java.util.List;
import java.util.UUID;

public interface ShippingAddressService {
    UUID createShippingAddress(ShippingAddress shippingAddress);

    void updateShippingAddress(ShippingAddress shippingAddress);

    ShippingAddress getShippingAddress(UUID id);

    List<ShippingAddress> getShippingAddressesByCustomer(UUID customerId);

    void deleteShippingAddress(UUID id);
}