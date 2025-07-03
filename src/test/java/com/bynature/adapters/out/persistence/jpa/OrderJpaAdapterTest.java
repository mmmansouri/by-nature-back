package com.bynature.adapters.out.persistence.jpa;

import com.bynature.adapters.out.persistence.jpa.adapter.CustomerRepositoryAdapter;
import com.bynature.adapters.out.persistence.jpa.adapter.ItemRepositoryAdapter;
import com.bynature.adapters.out.persistence.jpa.adapter.OrderRepositoryAdapter;
import com.bynature.domain.exception.OrderNotFoundException;
import com.bynature.domain.model.Customer;
import com.bynature.domain.model.Email;
import com.bynature.domain.model.Item;
import com.bynature.domain.model.Order;
import com.bynature.domain.model.OrderItem;
import com.bynature.domain.model.OrderStatus;
import com.bynature.domain.model.PhoneNumber;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ContextConfiguration;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ContextConfiguration(classes = {OrderRepositoryAdapter.class, ItemRepositoryAdapter.class, CustomerRepositoryAdapter.class})
public class OrderJpaAdapterTest extends AbstractJpaTest {

    @Autowired
    CustomerRepositoryAdapter customerRepositoryAdapter;

    @Autowired
    private OrderRepositoryAdapter orderRepositoryAdapter;

    @Autowired
    private ItemRepositoryAdapter itemRepositoryAdapter;

    private Item item1;
    private Item item2;
    private Customer customer;
    private List<OrderItem> orderItems;

    @BeforeEach
    public void setUp() {
        // Get test items
        item1 = itemRepositoryAdapter.getItem(UUID.fromString("bc9264a3-8d7e-4971-870e-3b745f20a7fa"));
        item2 = itemRepositoryAdapter.getItem(UUID.fromString("cc2aa61b-03ad-42df-b762-af3d5f5ae1ae"));
        orderItems = List.of(new OrderItem(item1, 2), new OrderItem(item2, 3));

        // Create test customer
        customer = customerRepositoryAdapter.getCustomer(UUID.fromString("f47ac10b-58cc-4372-a567-0e02b2c3d479"));
    }

    @Test
    public void whenSavingOrder_thenItCanBeRetrieved() {
        // Customer can be different from the one in the order
        Order order = new Order(customer, orderItems, 150.0,
                "Mohamed", "MANSOURI", new PhoneNumber("+33634164387"), new Email("toto@gmail.com"),
                "123", "Avenue de la redoute", "Asnières", "Haut de France",
                "92600", "France");

        // Save the order entity
        orderRepositoryAdapter.saveOrder(order);

        // Retrieve the order entity by its id
        Order retrievedOrder = orderRepositoryAdapter.getOrder(order.getId());
        assertThat(retrievedOrder)
                .isNotNull();
        assertThat(retrievedOrder.getId()).isEqualTo(order.getId());
        assertThat(retrievedOrder.getCustomer()).usingRecursiveComparison().isEqualTo(order.getCustomer());
        assertThat(retrievedOrder.getTotal()).isEqualTo(150.0);
        assertThat(retrievedOrder.getStatus()).isEqualTo(OrderStatus.CREATED);
        assertThat(retrievedOrder).isNotNull();
        assertThat(retrievedOrder.getCity()).isEqualTo("Asnières");
        assertThat(retrievedOrder.getCountry()).isEqualTo("France");
        assertThat(retrievedOrder.getPostalCode()).isEqualTo("92600");
        assertThat(retrievedOrder.getStreet()).isEqualTo("Avenue de la redoute");
        assertThat(retrievedOrder.getStreetNumber()).isEqualTo("123");
        assertThat(retrievedOrder.getRegion()).isEqualTo("Haut de France");
        assertThat(retrievedOrder.getOrderItems()).usingRecursiveComparison().isEqualTo(orderItems);
    }

    @Test
    public void whenUpdatingOrderStatus_thenStatusIsUpdated() {
        // Create and save an order
        Order order = new Order(customer, orderItems, 150.0,
                "Jane", "Smith", new PhoneNumber("+33698765432"), new Email("jane.smith@example.com"),
                "45", "Rue Victor Hugo", "Lyon", "Auvergne-Rhône-Alpes",
                "69002", "France");
        orderRepositoryAdapter.saveOrder(order);

        // Update the status
        orderRepositoryAdapter.updateOrderStatus(order.getId(), OrderStatus.PAYMENT_CONFIRMED);

        clearAndFlush();

        // Retrieve updated order
        Order updatedOrder = orderRepositoryAdapter.getOrder(order.getId());

        // Assert status was updated
        assertThat(updatedOrder.getStatus()).isEqualTo(OrderStatus.PAYMENT_CONFIRMED);
    }

    @Test
    public void whenUpdatingOrderStatusWithPaymentIntent_thenBothFieldsAreUpdated() {
        // Create and save an order
        Order order = new Order(customer, orderItems, 200.0,
                "Robert", "Johnson", new PhoneNumber("+33611223344"), new Email("robert.j@example.com"),
                "8", "Rue de Rivoli", "Paris", "Île-de-France",
                "75004", "France");
        orderRepositoryAdapter.saveOrder(order);

        // Update with payment intent
        String paymentIntentId = "pi_" + UUID.randomUUID().toString().replace("-", "");
        orderRepositoryAdapter.updateOrderStatus(order.getId(), OrderStatus.PAYMENT_PROCESSING, paymentIntentId);

        clearAndFlush();

        // Retrieve updated order
        Order updatedOrder = orderRepositoryAdapter.getOrder(order.getId());

        // Assert fields were updated
        assertThat(updatedOrder.getStatus()).isEqualTo(OrderStatus.PAYMENT_PROCESSING);
        assertThat(updatedOrder.getPaymentIntentId()).isEqualTo(paymentIntentId);
    }

    @Test
    public void whenFindingOrdersByCustomerId_thenOrdersAreReturned() {
        // Create and save multiple orders for the same customer
        Order order1 = new Order(customer, orderItems, 150.0,
                customer.getFirstName(), customer.getLastName(),
                customer.getPhoneNumber(), customer.getEmail(),
                customer.getStreetNumber(), customer.getStreet(), customer.getCity(),
                customer.getRegion(), customer.getPostalCode(), customer.getCountry());

        Order order2 = new Order(customer, orderItems, 200.0,
                customer.getFirstName(), customer.getLastName(),
                customer.getPhoneNumber(), customer.getEmail(),
                customer.getStreetNumber(), customer.getStreet(), customer.getCity(),
                customer.getRegion(), customer.getPostalCode(), customer.getCountry());

        orderRepositoryAdapter.saveOrder(order1);
        orderRepositoryAdapter.saveOrder(order2);

        // Find orders by customer ID
        List<Order> customerOrders = orderRepositoryAdapter.getOrdersByCustomer(customer.getId());

        // Assert orders are found
        assertThat(customerOrders).hasSize(2);
        assertThat(customerOrders.stream().map(Order::getId))
                .contains(order1.getId(), order2.getId());
    }

    @Test
    public void whenFindingOrdersByCustomerIdAsync_thenOrdersAreReturned() throws ExecutionException, InterruptedException, TimeoutException {
        // Create and save orders for the customer
        Order order = new Order(customer, orderItems, 300.0,
                customer.getFirstName(), customer.getLastName(),
                customer.getPhoneNumber(), customer.getEmail(),
                customer.getStreetNumber(), customer.getStreet(), customer.getCity(),
                customer.getRegion(), customer.getPostalCode(), customer.getCountry());

        orderRepositoryAdapter.saveOrder(order);

        // Find orders by customer ID asynchronously
        CompletableFuture<List<Order>> futureOrders = orderRepositoryAdapter.getOrdersByCustomerIdAsync(customer.getId());

        // Get the result with a timeout
        List<Order> customerOrders = futureOrders.get(5, TimeUnit.SECONDS);

        // Assert orders are found
        assertThat(customerOrders).isNotEmpty();
        assertThat(customerOrders.stream().map(Order::getId))
                .contains(order.getId());
    }

    @Test
    public void whenFindingOrdersByCustomerIdPaginated_thenPagedResultIsReturned() {
        // Create and save multiple orders
        for (int i = 0; i < 5; i++) {
            Order order = new Order(customer, orderItems, 100.0 + i * 50,
                    customer.getFirstName(), customer.getLastName(),
                    customer.getPhoneNumber(), customer.getEmail(),
                    customer.getStreetNumber(), customer.getStreet(), customer.getCity(),
                    customer.getRegion(), customer.getPostalCode(), customer.getCountry());
            orderRepositoryAdapter.saveOrder(order);
        }

        // Find first page with 2 orders
        Page<Order> firstPage = orderRepositoryAdapter.getOrdersByCustomerIdPaginated(
                customer.getId(), PageRequest.of(0, 2));

        // Find second page with 2 orders
        Page<Order> secondPage = orderRepositoryAdapter.getOrdersByCustomerIdPaginated(
                customer.getId(), PageRequest.of(1, 2));

        // Assert pagination works correctly
        assertThat(firstPage.getTotalElements()).isGreaterThanOrEqualTo(5);
        assertThat(firstPage.getContent()).hasSize(2);
        assertThat(secondPage.getContent()).hasSize(2);
        assertThat(firstPage.getContent().get(0).getId())
                .isNotEqualTo(secondPage.getContent().get(0).getId());
    }

    @Test
    public void whenFindingOrdersByCustomerIdAndStatus_thenFilteredOrdersAreReturned() {
        // Create and save orders with different statuses
        Order paidOrder = new Order(customer, orderItems, 150.0,
                customer.getFirstName(), customer.getLastName(),
                customer.getPhoneNumber(), customer.getEmail(),
                customer.getStreetNumber(), customer.getStreet(), customer.getCity(),
                customer.getRegion(), customer.getPostalCode(), customer.getCountry());

        Order shippedOrder = new Order(customer, orderItems, 200.0,
                customer.getFirstName(), customer.getLastName(),
                customer.getPhoneNumber(), customer.getEmail(),
                customer.getStreetNumber(), customer.getStreet(), customer.getCity(),
                customer.getRegion(), customer.getPostalCode(), customer.getCountry());

        orderRepositoryAdapter.saveOrder(paidOrder);
        orderRepositoryAdapter.saveOrder(shippedOrder);

        // Update their statuses
        orderRepositoryAdapter.updateOrderStatus(paidOrder.getId(), OrderStatus.PAYMENT_CONFIRMED);
        orderRepositoryAdapter.updateOrderStatus(shippedOrder.getId(), OrderStatus.SHIPPED);

        clearAndFlush();

        // Find orders with PAID status
        List<Order> paidOrders = orderRepositoryAdapter.getOrdersByCustomerAndStatus(
                customer.getId(), OrderStatus.PAYMENT_CONFIRMED);

        // Find orders with SHIPPED status
        List<Order> shippedOrders = orderRepositoryAdapter.getOrdersByCustomerAndStatus(
                customer.getId(), OrderStatus.SHIPPED);

        // Assert filtering works correctly
        assertThat(paidOrders).hasSize(1);
        assertThat(paidOrders.get(0).getId()).isEqualTo(paidOrder.getId());
        assertThat(paidOrders.get(0).getStatus()).isEqualTo(OrderStatus.PAYMENT_CONFIRMED);

        assertThat(shippedOrders).hasSize(1);
        assertThat(shippedOrders.get(0).getId()).isEqualTo(shippedOrder.getId());
        assertThat(shippedOrders.get(0).getStatus()).isEqualTo(OrderStatus.SHIPPED);
    }

    @Test
    public void whenOrderDoesNotExist_thenGetOrderReturnsNull() {
        // Try to retrieve a non-existent order
        OrderNotFoundException nonExistentOrder = assertThrows(OrderNotFoundException.class,
                () ->orderRepositoryAdapter.getOrder(UUID.randomUUID()));

        assertThat(nonExistentOrder.getOrderId()).isNotNull();
    }

    @Test
    public void whenNoOrdersForCustomer_thenEmptyListIsReturned() {
        // Create a random UUID that doesn't exist in the database
        UUID randomCustomerId = UUID.randomUUID();

        // Try to find orders for this customer
        List<Order> orders = orderRepositoryAdapter.getOrdersByCustomer(randomCustomerId);

        // Assert the list is empty
        assertThat(orders).isEmpty();
    }
}
