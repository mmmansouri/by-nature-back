package com.bynature.adapters.in.web;

import com.bynature.adapters.in.web.dto.request.OrderRequest;
import com.bynature.adapters.in.web.dto.request.ShippingAddressRequest;
import com.bynature.adapters.in.web.dto.response.OrderResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.net.URI;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class OrderControllerE2ETest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    public void whenCreateOrder_shouldRetrieveIT_E2E() {
        // Prepare a sample ShippingAddressRequest.
        ShippingAddressRequest addressRequest = new ShippingAddressRequest("Mohamed", "Mohamed",
                "+33634164387",
                "toto@gmail.com",
                "123", "Avenue de la redoute",
                "Asnières", "Haut de France",
                "92600", "France");

        // Prepare a sample OrderRequest.
        OrderRequest orderRequest = new OrderRequest();
        orderRequest.setCustomerId(UUID.randomUUID());
        orderRequest.setTotal(100.0);
        orderRequest.setStatus("NEW");
        orderRequest.setShippingAddress(addressRequest);
        // You can add orderItems if necessary.


        // Create HTTP headers and set the content type.
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // Wrap the OrderRequest in an HttpEntity.
        HttpEntity<OrderRequest> requestEntity = new HttpEntity<>(orderRequest, headers);

        // Execute the POST request to the /orders endpoint.
        ResponseEntity<UUID> responseOrderUUID = restTemplate.postForEntity("/orders", requestEntity, UUID.class);

        // Assert that we receive a 201 Created status.
        assertThat(responseOrderUUID.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        // Verify that the body contains the expected order ID.
        UUID orderUUID = responseOrderUUID.getBody();
        assertThat(orderUUID).isNotNull();

        // Verify the Location header is set correctly.
        URI location = responseOrderUUID.getHeaders().getLocation();
        assertThat(location).isNotNull();

        // Call the GET /orders/{id} endpoint
        ResponseEntity<OrderResponse> responseOrder = restTemplate.getForEntity("/orders/" + orderUUID, OrderResponse.class);

        // Assert the responseOrder
        assertThat(responseOrder.getStatusCode()).isEqualTo(HttpStatus.OK);
        OrderResponse orderResponse = responseOrder.getBody();
        assertThat(orderResponse).isNotNull();
        assertThat(orderResponse.getId()).isEqualTo(orderUUID);
        assertThat(orderResponse.getCustomerId()).isEqualTo(orderRequest.getCustomerId());
        assertThat(orderResponse.getTotal()).isEqualTo(orderRequest.getTotal());
        assertThat(orderResponse.getStatus()).isEqualTo(orderRequest.getStatus());
        assertThat(orderResponse.getShippingAddress()).isNotNull();
        assertThat(orderResponse.getShippingAddress().getFirstName()).isEqualTo(addressRequest.getFirstName());
        assertThat(orderResponse.getShippingAddress().getLastName()).isEqualTo(addressRequest.getLastName());
        assertThat(orderResponse.getShippingAddress().getPhoneNumber()).isEqualTo(addressRequest.getPhoneNumber());
        assertThat(orderResponse.getShippingAddress().getEmail()).isEqualTo(addressRequest.getEmail());
        assertThat(orderResponse.getShippingAddress().getCity()).isEqualTo(addressRequest.getCity());
        assertThat(orderResponse.getShippingAddress().getCountry()).isEqualTo(addressRequest.getCountry());
        assertThat(orderResponse.getShippingAddress().getPostalCode()).isEqualTo(addressRequest.getPostalCode());
        assertThat(orderResponse.getShippingAddress().getStreet()).isEqualTo(addressRequest.getStreet());
        assertThat(orderResponse.getShippingAddress().getStreetNumber()).isEqualTo(addressRequest.getStreetNumber());
        assertThat(orderResponse.getShippingAddress().getRegion()).isEqualTo(addressRequest.getRegion());

    }
}
