package com.bynature.adapters.out.persistence.jpa.adapter;

import com.bynature.adapters.out.persistence.jpa.adapter.mapper.EntityMapper;
import com.bynature.adapters.out.persistence.jpa.entity.CustomerEntity;
import com.bynature.adapters.out.persistence.jpa.repository.CustomerJpaRepository;
import com.bynature.domain.exception.CustomerNotFoundException;
import com.bynature.domain.model.Customer;
import com.bynature.domain.repository.CustomerRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;


@Repository
public class CustomerRepositoryAdapter implements CustomerRepository {

    private static final Logger log = LoggerFactory.getLogger(CustomerRepositoryAdapter.class);
    private final CustomerJpaRepository customerJpaRepository;

    public CustomerRepositoryAdapter(CustomerJpaRepository customerJpaRepository) {
        this.customerJpaRepository = customerJpaRepository;
    }

    @Override
    @Transactional
    public UUID saveCustomer(Customer customer) {
        log.debug("Saving customer with email: {}", customer.getEmail().email());
        return customerJpaRepository
                .save(CustomerEntity.fromDomain(customer))
                .getId();
    }

    @Override
    @Transactional
    public void updateCustomer(Customer customer) {
        log.debug("Updating customer with ID: {}", customer.getId());

        // Verify customer exists before updating
        getCustomer(customer.getId());

        customerJpaRepository
                .save(CustomerEntity.fromDomain(customer));

        log.info("Customer updated with ID: {}", customer.getId());
    }

    @Override
    @Transactional(readOnly = true)
    public Customer getCustomer(UUID customerId) {

        log.debug("Fetching customer with ID: {}", customerId);

        return customerJpaRepository.findById(customerId).map(EntityMapper::mapCustomerToDomain)
                .orElseThrow(() -> new CustomerNotFoundException("Customer not found with id: " + customerId));
    }

    @Override
    @Transactional
    public void deleteCustomer(UUID customerId) {

        log.debug("Deleting customer with ID: {}", customerId);

        // Verify customer exists before deleting
        getCustomer(customerId);

        customerJpaRepository.deleteById(customerId);

        log.info("Customer deleted with ID: {}", customerId);
    }

}
