package com.bynature.adapters.in.web.customer;

import com.bynature.domain.exception.CustomerNotFoundException;
import com.bynature.domain.model.Customer;
import com.bynature.domain.model.Email;
import com.bynature.domain.model.Role;
import com.bynature.domain.model.User;
import com.bynature.domain.service.CustomerService;
import com.bynature.domain.service.UserService;
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
    private final UserService userService;

    public CustomerController(CustomerService customerService, UserService userService) {
        this.customerService = customerService;
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<UUID> createCustomer(@Valid @RequestBody CustomerCreationRequest customerCreationRequest) {

        User createdUser = userService.createUser(new User(new Email(customerCreationRequest.email()),
                customerCreationRequest.password()
                , Role.CUSTOMER));

        Customer createdCustomer = customerService.createCustomer(customerCreationRequest.toDomain(createdUser));

        createdUser.linkToCustomer(createdCustomer);

        userService.updateUser(createdUser);

        return ResponseEntity
                .created(URI.create("/customers/" + createdCustomer.getId()))
                .body(createdCustomer.getId());
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