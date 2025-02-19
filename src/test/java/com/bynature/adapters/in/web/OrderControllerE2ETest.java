package com.bynature.adapters.in.web;

import com.bynature.AbstractByNatureTest;
import com.bynature.adapters.in.web.dto.request.OrderCreationRequest;
import com.bynature.adapters.in.web.dto.request.ShippingAddressCreationRequest;
import com.bynature.adapters.in.web.dto.response.OrderRetrievalResponse;
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
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class OrderControllerE2ETest extends AbstractByNatureTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    public void whenCreateOrder_shouldRetrieveIT_E2E() {
        // Prepare a sample ShippingAddressRequest.
        ShippingAddressCreationRequest addressRequest = new ShippingAddressCreationRequest("Mohamed", "Mohamed",
                "+33634164387",
                "toto@gmail.com",
                "123", "Avenue de la redoute",
                "Asnières", "Haut de France",
                "92600", "France");

        // Prepare a sample OrderRequest.
        OrderCreationRequest orderCreationRequest = new OrderCreationRequest(UUID.randomUUID(),
                Map.of(UUID.fromString("b3f9bfb5-90c1-4a8f-bab0-ac8bb355f3f1"), 2),
                100.0, "NEW",
                addressRequest);

        // Create HTTP headers and set the content type.
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // Wrap the OrderRequest in an HttpEntity.
        HttpEntity<OrderCreationRequest> requestEntity = new HttpEntity<>(orderCreationRequest, headers);

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
        ResponseEntity<OrderRetrievalResponse> responseOrder = restTemplate.getForEntity("/orders/" + orderUUID, OrderRetrievalResponse.class);

        // Assert the responseOrder
        assertThat(responseOrder.getStatusCode()).isEqualTo(HttpStatus.OK);
        OrderRetrievalResponse orderRetrievalResponse = responseOrder.getBody();
        assertThat(orderRetrievalResponse).isNotNull();
        assertThat(orderRetrievalResponse.id()).isEqualTo(orderUUID);
        assertThat(orderRetrievalResponse.customerId()).isEqualTo(orderCreationRequest.customerId());
        assertThat(orderRetrievalResponse.total()).isEqualTo(orderCreationRequest.total());
        assertThat(orderRetrievalResponse.status()).isEqualTo(orderCreationRequest.status());
        assertThat(orderRetrievalResponse.shippingAddress()).isNotNull();
        assertThat(orderRetrievalResponse.shippingAddress().firstName()).isEqualTo(addressRequest.firstName());
        assertThat(orderRetrievalResponse.shippingAddress().lastName()).isEqualTo(addressRequest.lastName());
        assertThat(orderRetrievalResponse.shippingAddress().phoneNumber()).isEqualTo(addressRequest.phoneNumber());
        assertThat(orderRetrievalResponse.shippingAddress().email()).isEqualTo(addressRequest.email());
        assertThat(orderRetrievalResponse.shippingAddress().city()).isEqualTo(addressRequest.city());
        assertThat(orderRetrievalResponse.shippingAddress().country()).isEqualTo(addressRequest.country());
        assertThat(orderRetrievalResponse.shippingAddress().postalCode()).isEqualTo(addressRequest.postalCode());
        assertThat(orderRetrievalResponse.shippingAddress().street()).isEqualTo(addressRequest.street());
        assertThat(orderRetrievalResponse.shippingAddress().streetNumber()).isEqualTo(addressRequest.streetNumber());
        assertThat(orderRetrievalResponse.shippingAddress().region()).isEqualTo(addressRequest.region());

    }
}
