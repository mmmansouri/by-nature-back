package com.bynature.domain.repository;

import com.bynature.domain.model.Customer;

import java.util.UUID;

public interface CustomerRepository {
    UUID saveCustomer(Customer customer);

    void updateCustomer(Customer customer);

    Customer getCustomer(UUID customerId);

    void deleteCustomer(UUID customerId);
}
