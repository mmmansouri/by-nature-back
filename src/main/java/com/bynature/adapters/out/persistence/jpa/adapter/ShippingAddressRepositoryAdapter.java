package com.bynature.adapters.out.persistence.jpa.adapter;

import com.bynature.adapters.out.persistence.jpa.entity.CustomerEntity;
import com.bynature.adapters.out.persistence.jpa.entity.ShippingAddressEntity;
import com.bynature.adapters.out.persistence.jpa.repository.ShippingAddressJpaRepository;
import com.bynature.domain.exception.ShippingAddressNotFoundException;
import com.bynature.domain.model.ShippingAddress;
import com.bynature.domain.repository.ShippingAddressRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Repository
public class ShippingAddressRepositoryAdapter implements ShippingAddressRepository {

    private static final Logger log = LoggerFactory.getLogger(ShippingAddressRepositoryAdapter.class);

    private final ShippingAddressJpaRepository shippingAddressJpaRepository;

    public ShippingAddressRepositoryAdapter(ShippingAddressJpaRepository shippingAddressJpaRepository) {
        this.shippingAddressJpaRepository = shippingAddressJpaRepository;
    }

    @Override
    @Transactional
    public UUID saveShippingAddress(ShippingAddress shippingAddress) {
        log.debug("Saving shipping address with ID: {}", shippingAddress.getId());

        ShippingAddressEntity entity = mapToEntity(shippingAddress);
        ShippingAddressEntity savedEntity = shippingAddressJpaRepository.save(entity);

        log.info("Shipping address saved with ID: {}", savedEntity.getId());
        return savedEntity.getId();
    }

    @Override
    @Transactional
    public void updateShippingAddress(ShippingAddress shippingAddress) {
        log.debug("Updating shipping address with ID: {}", shippingAddress.getId());

        // Verify shipping address exists before updating
        getShippingAddress(shippingAddress.getId());

        ShippingAddressEntity entity = mapToEntity(shippingAddress);
        shippingAddressJpaRepository.save(entity);

        log.info("Shipping address updated with ID: {}", shippingAddress.getId());
    }

    @Override
    @Transactional(readOnly = true)
    public ShippingAddress getShippingAddress(UUID id) {
        log.info("Fetching shipping address with ID: {}", id);

        Optional<ShippingAddressEntity> optionalEntity = shippingAddressJpaRepository.findById(id);

        return optionalEntity.map(this::mapToDomain)
                .orElseThrow(() -> new ShippingAddressNotFoundException("Shipping address not found with id: " + id, id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ShippingAddress> getShippingAddressesByCustomer(UUID customerId) {
        log.debug("Fetching shipping addresses for customer ID: {}", customerId);

        return shippingAddressJpaRepository.findByCustomerId(customerId)
                .stream()
                .map(this::mapToDomain)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteShippingAddress(UUID id) {
        log.debug("Deleting shipping address with ID: {}", id);

        // Verify shipping address exists before deleting
        getShippingAddress(id);
        shippingAddressJpaRepository.deleteById(id);

        log.info("Shipping address deleted with ID: {}", id);
    }

    private ShippingAddressEntity mapToEntity(ShippingAddress shippingAddress) {
        return new ShippingAddressEntity(
                shippingAddress.getId(),
                CustomerEntity.fromDomain(shippingAddress.getCustomer()),
                shippingAddress.getLabel(),
                shippingAddress.getFirstName(),
                shippingAddress.getLastName(),
                shippingAddress.getPhoneNumber().number(),
                shippingAddress.getEmail().email(),
                shippingAddress.getStreetNumber(),
                shippingAddress.getStreet(),
                shippingAddress.getCity(),
                shippingAddress.getRegion(),
                shippingAddress.getPostalCode(),
                shippingAddress.getCountry(),
                shippingAddress.getCreatedAt(),
                shippingAddress.getUpdatedAt()
        );
    }

    private ShippingAddress mapToDomain(ShippingAddressEntity entity) {
        return entity.toDomain();
    }

    public CompletableFuture<List<ShippingAddress>> getShippingAddressesByCustomerIdAsync(UUID customerId) {
        return shippingAddressJpaRepository.findByCustomerIdAsync(customerId)
                .thenApply(addresses -> addresses.stream()
                        .map(this::mapToDomain)
                        .collect(Collectors.toList()));
    }

    public Page<ShippingAddress> getShippingAddressesByCustomerIdPaginated(UUID customerId, PageRequest pageRequest) {
        Page<ShippingAddressEntity> addressPage = shippingAddressJpaRepository.findByCustomer_Id(customerId, pageRequest);
        return addressPage.map(this::mapToDomain);
    }
}