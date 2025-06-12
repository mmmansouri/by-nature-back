package com.bynature.adapters.out.persistence.jpa.adapter.mapper;


import com.bynature.adapters.out.persistence.jpa.entity.CustomerEntity;
import com.bynature.adapters.out.persistence.jpa.entity.ItemEntity;
import com.bynature.adapters.out.persistence.jpa.entity.OrderEntity;
import com.bynature.adapters.out.persistence.jpa.entity.ShippingAddressEntity;
import com.bynature.adapters.out.persistence.jpa.entity.UserEntity;
import com.bynature.domain.model.Customer;
import com.bynature.domain.model.Email;
import com.bynature.domain.model.Item;
import com.bynature.domain.model.Order;
import com.bynature.domain.model.OrderItem;
import com.bynature.domain.model.PhoneNumber;
import com.bynature.domain.model.Role;
import com.bynature.domain.model.ShippingAddress;
import com.bynature.domain.model.User;

import java.util.List;

/**
 * Utility class for mapping between JPA entities and domain models.
 */
public final class EntityMapper {

    // Private constructor to prevent instantiation
    private EntityMapper() {}

    public static User mapUserToDomain(UserEntity entity) {
        if (entity == null) return null;

        // Create user without the customer relationship
        User user = new User(
                entity.getId(),
                new Email(entity.getEmail()),
                entity.getPassword(),
                null, // Initially no customer
                entity.isActive(),
                Role.valueOf(entity.getRole()),
                entity.getLastLoginAt(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );

        // If customer exists, map it and establish bidirectional relationship
        if (entity.getCustomer() != null) {
            CustomerEntity customerEntity = entity.getCustomer();
            Customer customer = createCustomerWithoutUser(customerEntity);
            user.setCustomer(customer);
            customer.setUser(user);
        }

        return user;
    }

    public static Customer mapCustomerToDomain(CustomerEntity entity) {
        if (entity == null) return null;

        // Create customer without the user relationship
        Customer customer = createCustomerWithoutUser(entity);

        // If user exists, map it and establish bidirectional relationship
        if (entity.getUser() != null) {
            UserEntity userEntity = entity.getUser();
            User user = new User(
                    userEntity.getId(),
                    new Email(userEntity.getEmail()),
                    userEntity.getPassword(),
                    customer, // Set customer directly here
                    userEntity.isActive(),
                    Role.valueOf(userEntity.getRole()),
                    userEntity.getLastLoginAt(),
                    userEntity.getCreatedAt(),
                    userEntity.getUpdatedAt()
            );
            customer.setUser(user);
        }

        return customer;
    }

    // Helper method to create customer object without setting user
    private static Customer createCustomerWithoutUser(CustomerEntity entity) {
        Customer customer = new Customer(
                entity.getId(),
                null, // No user set
                entity.getFirstName(),
                entity.getLastName(),
                new Email(entity.getEmail()),
                new PhoneNumber(entity.getPhoneNumber()),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );

        // Set optional address fields
        customer.setStreetNumber(entity.getStreetNumber());
        customer.setStreet(entity.getStreet());
        customer.setCity(entity.getCity());
        customer.setRegion(entity.getRegion());
        customer.setPostalCode(entity.getPostalCode());
        customer.setCountry(entity.getCountry());

        return customer;
    }

    // Item mapping
    public static Item mapItemToDomain(ItemEntity entity) {
        if (entity == null) return null;

        return new Item(
                entity.getId(),
                entity.getName(),
                entity.getDescription(),
                entity.getPrice(),
                entity.getImageUrl(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }

    // Order mapping
    public static Order mapOrderToDomain(OrderEntity entity) {
        if (entity == null) return null;

        List<OrderItem> orderItems = entity.getOrderItems().stream()
                .map(orderItemEntity -> new OrderItem(
                        mapItemToDomain(orderItemEntity.getItem()),
                        orderItemEntity.getQuantity()))
                .toList();

        Order order = new Order(
                entity.getId(),
                mapCustomerToDomain(entity.getCustomer()),
                orderItems,
                entity.getTotal(),
                entity.getStatus(),
                entity.getFirstName(),
                entity.getLastName(),
                new PhoneNumber(entity.getPhoneNumber()),
                new Email(entity.getEmail()),
                entity.getStreetNumber(),
                entity.getStreet(),
                entity.getCity(),
                entity.getRegion(),
                entity.getPostalCode(),
                entity.getCountry(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );

        order.setPaymentIntentId(entity.getPaymentIntentId());
        return order;
    }

    // ShippingAddress mapping
    public static ShippingAddress mapShippingAddressToDomain(ShippingAddressEntity entity) {
        if (entity == null) return null;

        return new ShippingAddress(
                entity.getId(),
                mapCustomerToDomain(entity.getCustomer()),
                entity.getLabel(),
                entity.getFirstName(),
                entity.getLastName(),
                new PhoneNumber(entity.getPhoneNumber()),
                new Email(entity.getEmail()),
                entity.getStreetNumber(),
                entity.getStreet(),
                entity.getCity(),
                entity.getRegion(),
                entity.getPostalCode(),
                entity.getCountry(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }
}