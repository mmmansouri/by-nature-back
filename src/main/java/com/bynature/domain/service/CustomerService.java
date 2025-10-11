package com.bynature.domain.service;

import com.bynature.domain.model.Customer;

import java.util.UUID;

public interface CustomerService {
    Customer createCustomer(Customer customer);

    void updateCustomer(Customer customer);

    void deleteCustomer(UUID customerId);

    Customer getCustomer(UUID customerId);

}
