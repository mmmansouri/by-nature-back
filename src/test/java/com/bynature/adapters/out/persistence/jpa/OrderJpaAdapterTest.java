package com.bynature.adapters.out.persistence.jpa;

import com.bynature.domain.model.Email;
import com.bynature.domain.model.Order;
import com.bynature.domain.model.PhoneNumber;
import com.bynature.domain.model.ShippingAddress;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ContextConfiguration;

import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@EnableAutoConfiguration
@ContextConfiguration(classes = {OrderRepositoryAdapter.class})
public class OrderJpaAdapterTest {

    @Autowired
    private OrderRepositoryAdapter orderRepositoryAdapter;

    @Test
    public void whenSavingOrder_thenItCanBeRetrieved() {

        Order order = new Order(UUID.randomUUID(), UUID.randomUUID(),Map.of(UUID.randomUUID(), 2) ,150.0, "NEW",
                new ShippingAddress("Mohamed", "Mohamed", new PhoneNumber("+33634164387"),new Email("toto@gmail.com"),"123", "Avenue de la redoute",
                        "Asnières","Haut de France","92600", "France" ));


        // Save the order entity
        orderRepositoryAdapter.saveOrder(order);

        // Retrieve the order entity by its id
        Order retrievedOrder = orderRepositoryAdapter.getOrder(order.getId());
        assertThat(retrievedOrder)
                .isNotNull();
        assertThat(retrievedOrder.getId()).isEqualTo(order.getId());
        assertThat(retrievedOrder.getCustomerId()).isEqualTo(order.getCustomerId());
        assertThat(retrievedOrder.getTotal()).isEqualTo(150.0);
        assertThat(retrievedOrder.getStatus()).isEqualTo("NEW");
        assertThat(retrievedOrder.getShippingAddress()).isNotNull();
        assertThat(retrievedOrder.getShippingAddress().getCity()).isEqualTo("Asnières");
        assertThat(retrievedOrder.getShippingAddress().getCountry()).isEqualTo("France");
        assertThat(retrievedOrder.getShippingAddress().getPostalCode()).isEqualTo("92600");
        assertThat(retrievedOrder.getShippingAddress().getStreet()).isEqualTo("Avenue de la redoute");
        assertThat(retrievedOrder.getShippingAddress().getStreetNumber()).isEqualTo("123");
        assertThat(retrievedOrder.getShippingAddress().getRegion()).isEqualTo("Haut de France");

    }
}
