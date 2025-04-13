package com.bynature.adapters.out.persistence.jpa;

import com.bynature.AbstractByNatureTest;
import com.bynature.adapters.out.persistence.jpa.adapter.ItemRepositoryAdapter;
import com.bynature.adapters.out.persistence.jpa.adapter.OrderRepositoryAdapter;
import com.bynature.domain.model.Customer;
import com.bynature.domain.model.Email;
import com.bynature.domain.model.Item;
import com.bynature.domain.model.Order;
import com.bynature.domain.model.OrderItem;
import com.bynature.domain.model.OrderStatus;
import com.bynature.domain.model.PhoneNumber;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ContextConfiguration;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@EnableAutoConfiguration
@ContextConfiguration(classes = {OrderRepositoryAdapter.class, ItemRepositoryAdapter.class})
public class OrderJpaAdapterTest extends AbstractByNatureTest {

    @Autowired
    private OrderRepositoryAdapter orderRepositoryAdapter;

    @Autowired
    private ItemRepositoryAdapter itemRepositoryAdapter;

    @Test
    public void whenSavingOrder_thenItCanBeRetrieved() {

        Item item1 = itemRepositoryAdapter.getItem(UUID.fromString("bc9264a3-8d7e-4971-870e-3b745f20a7fa"));
        Item item2 = itemRepositoryAdapter.getItem(UUID.fromString("cc2aa61b-03ad-42df-b762-af3d5f5ae1ae"));

        List<OrderItem> orderItems = List.of( new OrderItem(item1, 2), new OrderItem(item2,3));

        Customer customer = new Customer(UUID.fromString("f47ac10b-58cc-4372-a567-0e02b2c3d479"),
                "John","Doe", new Email("john.doe@example.com"),
                new PhoneNumber("+33612345678"));
        customer.setCity("Paris");
        customer.setStreet("Rue de la Paix");
        customer.setStreetNumber("42");
        customer.setRegion("Île-de-France");
        customer.setPostalCode("75001");
        customer.setCountry("France");

        // Customer can be different from the one in the order
        Order order = new Order(customer, orderItems ,150.0,
                "Mohamed", "MANSOURI", new PhoneNumber("+33634164387"),new Email("toto@gmail.com"),
                "123", "Avenue de la redoute", "Asnières","Haut de France",
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
}
