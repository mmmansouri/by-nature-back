package com.bynature.application.service;

import com.bynature.domain.model.Customer;
import com.bynature.domain.repository.CustomerRepository;
import com.bynature.domain.service.CustomerService;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class CustomerSpringService implements CustomerService {

    private final CustomerRepository customerRepository;

    public CustomerSpringService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    public void deleteCustomer(UUID customerId) {
        customerRepository.deleteCustomer(customerId);
    }

    public void updateCustomer(Customer customer) {
        customerRepository.updateCustomer(customer);
    }

    public UUID createCustomer(Customer customer) {
        return customerRepository.saveCustomer(customer);
    }

    public Customer getCustomer(UUID customerId) {
        return customerRepository.getCustomer(customerId);
    }

}
