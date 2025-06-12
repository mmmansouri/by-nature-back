package com.bynature.domain.model;

import com.bynature.domain.exception.OrderValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class OrderTest {

    private Customer validCustomer;
    private List<OrderItem> validOrderItems;
    private PhoneNumber validPhone;
    private Email validEmail;
    private User validUser;
    private final String validPassword = "securePassword123";
    private final Role validRole = Role.CUSTOMER;

    @BeforeEach
    void setUp() {
        validPhone = new PhoneNumber("+33612345678");
        validEmail = new Email("test@example.com");
        validUser = new User(validEmail, validPassword, validRole);

        validCustomer = new Customer(validUser,"John", "Doe",
                validEmail,
                validPhone,
                "123", "Main Street", "Paris", "Île-de-France",
                "75001", "France"
                );

        var item = new Item( "Test Item", "Description", 100.0,"testUrl");
        validOrderItems = List.of(new OrderItem(item, 2));
    }

    @Test
    @DisplayName("Should create order with valid data")
    void shouldCreateOrderWithValidData() {
        var order = assertDoesNotThrow(() -> new Order(
                validCustomer, validOrderItems, 200.0,
                "John", "Doe", validPhone, validEmail,
                "123", "Main Street", "Paris", "Île-de-France",
                "75001", "France"));

        assertThat(order.getId()).isNotNull();
        assertThat(order.getCustomer()).isEqualTo(validCustomer);
        assertThat(order.getOrderItems()).isEqualTo(validOrderItems);
        assertThat(order.getTotal()).isEqualTo(200.0);
        assertThat(order.getStatus()).isEqualTo(OrderStatus.CREATED);
        assertThat(order.getFirstName()).isEqualTo("John");
        assertThat(order.getLastName()).isEqualTo("Doe");
        assertThat(order.getCreatedAt()).isNotNull();
        assertThat(order.getUpdatedAt()).isNotNull();
    }

    @Test
    @DisplayName("Should update order status")
    void shouldUpdateOrderStatus() throws InterruptedException {
        var order = new Order(
                validCustomer, validOrderItems, 200.0,
                "John", "Doe", validPhone, validEmail,
                "123", "Main Street", "Paris", "Île-de-France",
                "75001", "France");

        var previousUpdatedAt = order.getUpdatedAt();

        Thread.sleep(3L);
        order.updateStatus(OrderStatus.PAYMENT_CONFIRMED);

        assertThat(order.getStatus()).isEqualTo(OrderStatus.PAYMENT_CONFIRMED);
        assertThat(order.getUpdatedAt()).isAfter(previousUpdatedAt);
    }

    @Test
    @DisplayName("Should set payment intent ID")
    void shouldSetPaymentIntentId() throws InterruptedException {
        var order = new Order(
                validCustomer, validOrderItems, 200.0,
                "John", "Doe", validPhone, validEmail,
                "123", "Main Street", "Paris", "Île-de-France",
                "75001", "France");

        var previousUpdatedAt = order.getUpdatedAt();
        var paymentIntentId = "pi_123456789";

        Thread.sleep(3L);
        order.setPaymentIntentId(paymentIntentId);

        assertThat(order.getPaymentIntentId()).isEqualTo(paymentIntentId);
        assertThat(order.getUpdatedAt()).isAfter(previousUpdatedAt);
    }

    @Test
    @DisplayName("Should throw exception when customer is null")
    void shouldThrowExceptionWhenCustomerIsNull() {
        assertThatExceptionOfType(OrderValidationException.class)
                .isThrownBy(() -> new Order(
                        null, validOrderItems, 200.0,
                        "John", "Doe", validPhone, validEmail,
                        "123", "Main Street", "Paris", "Île-de-France",
                        "75001", "France"))
                .satisfies(e -> assertThat(e.getViolations())
                        .contains("L'ID du client ne peut pas être null"));
    }

    @Test
    @DisplayName("Should throw exception when order items are empty")
    void shouldThrowExceptionWhenOrderItemsAreEmpty() {
        assertThatExceptionOfType(OrderValidationException.class)
                .isThrownBy(() -> new Order(
                        validCustomer, List.of(), 200.0,
                        "John", "Doe", validPhone, validEmail,
                        "123", "Main Street", "Paris", "Île-de-France",
                        "75001", "France"))
                .satisfies(e -> assertThat(e.getViolations())
                        .contains("La liste des articles ne peut pas être vide"));
    }

    @ParameterizedTest
    @ValueSource(doubles = {0.0, -1.0, -100.0})
    @DisplayName("Should throw exception when total is not positive")
    void shouldThrowExceptionWhenTotalIsNotPositive(double invalidTotal) {
        assertThatExceptionOfType(OrderValidationException.class)
                .isThrownBy(() -> new Order(
                        validCustomer, validOrderItems, invalidTotal,
                        "John", "Doe", validPhone, validEmail,
                        "123", "Main Street", "Paris", "Île-de-France",
                        "75001", "France"))
                .satisfies(e -> assertThat(e.getViolations())
                        .contains("Le total doit être positif"));
    }

    @Test
    @DisplayName("Should have updatedAt equal to createdAt upon initial creation")
    void shouldHaveUpdatedAtEqualToCreatedAtUponInitialCreation() {
        var order = new Order(
                validCustomer, validOrderItems, 200.0,
                "John", "Doe", validPhone, validEmail,
                "123", "Main Street", "Paris", "Île-de-France",
                "75001", "France");

        assertThat(order.getUpdatedAt()).isEqualTo(order.getCreatedAt());
    }

    @Test
    @DisplayName("Should allow updatedAt to be equal to createdAt")
    void shouldAllowUpdatedAtToBeEqualToCreatedAt() {
        var order = new Order(
                validCustomer, validOrderItems, 200.0,
                "John", "Doe", validPhone, validEmail,
                "123", "Main Street", "Paris", "Île-de-France",
                "75001", "France");

        assertDoesNotThrow(() -> {
            order.setUpdatedAt(order.getCreatedAt());
        });
    }

    @Test
    @DisplayName("Should allow updatedAt to be after createdAt")
    void shouldAllowUpdatedAtToBeAfterCreatedAt() {
        var order = new Order(
                validCustomer, validOrderItems, 200.0,
                "John", "Doe", validPhone, validEmail,
                "123", "Main Street", "Paris", "Île-de-France",
                "75001", "France");

        assertDoesNotThrow(() -> {
            order.setUpdatedAt(order.getCreatedAt().plusSeconds(1));
        });
    }

    @Test
    @DisplayName("Should reject updatedAt before createdAt")
    void shouldRejectUpdatedAtBeforeCreatedAt() {
        var order = new Order(
                validCustomer, validOrderItems, 200.0,
                "John", "Doe", validPhone, validEmail,
                "123", "Main Street", "Paris", "Île-de-France",
                "75001", "France");

        assertThatExceptionOfType(OrderValidationException.class)
                .isThrownBy(() -> {
                    order.setUpdatedAt(order.getCreatedAt().minusSeconds(1));
                })
                .satisfies(e -> assertThat(e.getViolations())
                        .contains("La date de mise à jour ne peut pas être avant celle de la création"));
    }
}