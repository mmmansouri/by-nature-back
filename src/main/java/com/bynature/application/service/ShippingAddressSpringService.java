package com.bynature.application.service;

import com.bynature.domain.model.ShippingAddress;
import com.bynature.domain.repository.ShippingAddressRepository;
import com.bynature.domain.service.ShippingAddressService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class ShippingAddressSpringService implements ShippingAddressService {

    private final ShippingAddressRepository shippingAddressRepository;

    public ShippingAddressSpringService(ShippingAddressRepository shippingAddressRepository) {
        this.shippingAddressRepository = shippingAddressRepository;
    }

    @Override
    public UUID createShippingAddress(ShippingAddress shippingAddress) {
        return shippingAddressRepository.saveShippingAddress(shippingAddress);
    }

    @Override
    public void updateShippingAddress(ShippingAddress shippingAddress) {
        shippingAddress.setUpdatedAt(LocalDateTime.now());
        shippingAddressRepository.updateShippingAddress(shippingAddress);
    }

    @Override
    public ShippingAddress getShippingAddress(UUID id) {
        return shippingAddressRepository.getShippingAddress(id);
    }

    @Override
    public List<ShippingAddress> getShippingAddressesByCustomer(UUID customerId) {
        return shippingAddressRepository.getShippingAddressesByCustomer(customerId);
    }

    @Override
    public void deleteShippingAddress(UUID id) {
        shippingAddressRepository.deleteShippingAddress(id);
    }
}