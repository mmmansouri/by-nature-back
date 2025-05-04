package com.bynature.adapters.in.web.customer;

import com.bynature.domain.exception.CustomerNotFoundException;
import com.bynature.domain.model.Customer;
import com.bynature.domain.service.CustomerService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.UUID;

@RestController
@RequestMapping("/customers")
public class CustomerController {

    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @PostMapping
    public ResponseEntity<UUID> createCustomer(@Valid @RequestBody CustomerCreationRequest customerCreationRequest) {
        UUID createdCustomerId = customerService.createCustomer(customerCreationRequest.toDomain());

        return ResponseEntity
                .created(URI.create("/customers/" + createdCustomerId))
                .body(createdCustomerId);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CustomerRetrievalResponse> getCustomer(@PathVariable("id") UUID uuid) {
        try {
            Customer customer = customerService.getCustomer(uuid);
            return ResponseEntity.ok(CustomerRetrievalResponse.fromDomain(customer));
        } catch (CustomerNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }
}