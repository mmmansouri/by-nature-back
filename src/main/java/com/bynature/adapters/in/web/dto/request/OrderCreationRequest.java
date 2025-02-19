package com.bynature.adapters.in.web.dto.request;

import com.bynature.domain.model.Email;
import com.bynature.domain.model.Order;
import com.bynature.domain.model.PhoneNumber;

import java.util.Map;
import java.util.UUID;

public record OrderCreationRequest( UUID customerId,
                                    Map<UUID, Integer> orderItems,
                                    double total,
                                    String status,
                                    ShippingAddressCreationRequest shippingAddress) {

    public Order toDomain() {
        return new Order(this.customerId,
                this.orderItems,
                this.total,
                this.status,
                this.shippingAddress.firstName(),
                this.shippingAddress.lastName(),
                new PhoneNumber(this.shippingAddress.phoneNumber()),
                new Email(this.shippingAddress.email()),
                this.shippingAddress.streetNumber(),
                this.shippingAddress.street(),
                this.shippingAddress.city(),
                this.shippingAddress.region(),
                this.shippingAddress.postalCode(),
                this.shippingAddress.country());
    }
}
