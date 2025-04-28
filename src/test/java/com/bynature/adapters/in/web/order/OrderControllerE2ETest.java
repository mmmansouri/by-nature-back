package com.bynature.adapters.in.web.order;

import com.bynature.AbstractByNatureTest;
import com.bynature.adapters.in.web.order.dto.request.OrderCreationRequest;
import com.bynature.adapters.in.web.order.dto.request.OrderItemCreationRequest;
import com.bynature.adapters.in.web.order.dto.request.ShippingAddressCreationRequest;
import com.bynature.adapters.in.web.order.dto.response.OrderRetrievalResponse;
import com.bynature.adapters.out.persistence.jpa.adapter.ItemRepositoryAdapter;
import com.bynature.domain.model.OrderStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class OrderControllerE2ETest extends AbstractByNatureTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private ItemRepositoryAdapter itemRepositoryAdapter;

    // Valid UUID constants for testing
    private static final UUID VALID_CUSTOMER_ID = UUID.fromString("f47ac10b-58cc-4372-a567-0e02b2c3d479");
    private static final UUID VALID_ITEM_ID = UUID.fromString("4ad102fd-bf4a-439f-8027-5c3cf527ffaf");

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
        OrderCreationRequest orderCreationRequest = new OrderCreationRequest(VALID_CUSTOMER_ID,
                List.of(new OrderItemCreationRequest(VALID_ITEM_ID, 2)),
                100.0,
                addressRequest);

        // Create HTTP headers and set the content type.
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // Wrap the OrderRequest in an HttpEntity.
        HttpEntity<OrderCreationRequest> requestEntity = new HttpEntity<>(orderCreationRequest, headers);

        // Execute the POST request to the /orders endpoint.
        ResponseEntity<OrderRetrievalResponse> responseOrder = restTemplate.postForEntity("/orders", requestEntity, OrderRetrievalResponse.class);

        // Assert that we receive a 201 Created status.
        assertThat(responseOrder.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        // Verify that the body contains the expected order ID.
        OrderRetrievalResponse orderResponse = responseOrder.getBody();
        assertThat(orderResponse).isNotNull();

        // Verify the Location header is set correctly.
        URI location = responseOrder.getHeaders().getLocation();
        assertThat(location).isNotNull();

        // Call the GET /orders/{id} endpoint
        responseOrder = restTemplate.getForEntity("/orders/" + orderResponse.id(), OrderRetrievalResponse.class);

        // Assert the responseOrder
        assertThat(responseOrder.getStatusCode()).isEqualTo(HttpStatus.OK);
        OrderRetrievalResponse orderRetrievalResponse = responseOrder.getBody();
        assertThat(orderRetrievalResponse).isNotNull();
        assertThat(orderRetrievalResponse.id()).isEqualTo(orderResponse.id());
        assertThat(orderRetrievalResponse.customer()).isEqualTo(orderCreationRequest.customerId());
        assertThat(orderRetrievalResponse.total()).isEqualTo(orderCreationRequest.total());
        assertThat(orderRetrievalResponse.status()).isEqualTo(OrderStatus.CREATED.toString());
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

    @Test
    void whenFetchNonExistentOrder_thenReturnNotFound_E2E() {
        // Generate a random UUID that shouldn't exist in the database
        UUID nonExistentId = UUID.randomUUID();
        
        // Call the GET /orders/{id} endpoint with a non-existent ID
        ResponseEntity<String> response = restTemplate.getForEntity(
                "/orders/" + nonExistentId, 
                String.class
        );
        
        // Verify HTTP 404 Not Found status
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        
        // Verify error response contains the orderId
        assertThat(response.getBody()).contains(nonExistentId.toString());
    }

    
    private static Stream<Arguments> invalidOrderRequests() {
        return Stream.of(
            // Case: Missing customer ID
            Arguments.of(
                "Missing customer ID",
                new OrderCreationRequest(
                    null,
                    List.of(new OrderItemCreationRequest(VALID_ITEM_ID, 2)),
                    100.0,
                    createValidShippingAddress()
                )
            ),
            // Case: Empty order items
            Arguments.of(
                "Empty order items", 
                new OrderCreationRequest(
                    VALID_CUSTOMER_ID,
                    Collections.emptyList(),
                    100.0,
                    createValidShippingAddress()
                )
            ),
            // Case: Invalid quantity (zero)
            Arguments.of(
                "Zero quantity", 
                new OrderCreationRequest(
                    VALID_CUSTOMER_ID,
                    List.of(new OrderItemCreationRequest(VALID_ITEM_ID, 0)),
                    100.0,
                    createValidShippingAddress()
                )
            ),
            // Case: Negative quantity
            Arguments.of(
                "Negative quantity", 
                new OrderCreationRequest(
                    VALID_CUSTOMER_ID,
                    List.of(new OrderItemCreationRequest(VALID_ITEM_ID, -1)),
                    100.0,
                    createValidShippingAddress()
                )
            ),
            // Case: Missing shipping address
            Arguments.of(
                "Missing shipping address", 
                new OrderCreationRequest(
                    VALID_CUSTOMER_ID,
                    List.of(new OrderItemCreationRequest(VALID_ITEM_ID, 2)),
                    100.0,
                    null
                )
            ),
            // Case: Invalid email in shipping address
            Arguments.of(
                "Invalid email", 
                new OrderCreationRequest(
                    VALID_CUSTOMER_ID,
                    List.of(new OrderItemCreationRequest(VALID_ITEM_ID, 2)),
                    100.0,
                    new ShippingAddressCreationRequest(
                        "John", "Doe", "+33634164387", "invalid-email",
                        "123", "Main Street", "Paris", "Ile-de-France",
                        "75001", "France"
                    )
                )
            )
        );
    }

    @ParameterizedTest(name = "Invalid order validation: {0}")
    @MethodSource("invalidOrderRequests")
    void whenCreateInvalidOrder_thenReturnBadRequest_E2E(String testName, OrderCreationRequest invalidRequest) {
        // Execute the POST request with invalid data
        ResponseEntity<String> response = restTemplate.postForEntity(
                "/orders",
                new HttpEntity<>(invalidRequest, createJsonHeaders()),
                String.class
        );
        
        // Verify HTTP 400 Bad Request status
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }
    
    @Test
    void whenFetchOrdersByCustomer_thenReturnMatchingOrders_E2E() {
        // Create an order for our customer
        OrderCreationRequest orderRequest = createValidOrderRequest();
        ResponseEntity<OrderRetrievalResponse> creationResponse = restTemplate.postForEntity(
                "/orders", 
                new HttpEntity<>(orderRequest, createJsonHeaders()),
                OrderRetrievalResponse.class
        );
        
        // Verify the order was created
        assertThat(creationResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        
        // Build URL with query parameter
        String url = UriComponentsBuilder.fromPath("/orders/customer/{id}")
                .buildAndExpand(VALID_CUSTOMER_ID)
                .toUriString();
                
        // Fetch orders by customer ID
        ResponseEntity<OrderRetrievalResponse[]> response = restTemplate.getForEntity(
                url,
                OrderRetrievalResponse[].class
        );
        
        // Verify successful response
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        OrderRetrievalResponse[] orders = response.getBody();
        assertThat(orders).isNotNull();
        
        // Verify at least one order was returned
        assertThat(orders.length).isGreaterThan(0);
        
        // All returned orders should belong to our customer
        for (OrderRetrievalResponse order : orders) {
            assertThat(order.customer()).isEqualTo(VALID_CUSTOMER_ID);
        }
    }

    // Helper methods
    private static HttpHeaders createJsonHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }
    
    private static ShippingAddressCreationRequest createValidShippingAddress() {
        return new ShippingAddressCreationRequest(
                "John", "Doe",
                "+33634164387",
                "valid@example.com",
                "123", "Main Street",
                "Paris", "Ile-de-France",
                "75001", "France"
        );
    }
    
    private static OrderCreationRequest createValidOrderRequest() {
        return new OrderCreationRequest(
                VALID_CUSTOMER_ID,
                List.of(new OrderItemCreationRequest(VALID_ITEM_ID, 2)),
                100.0,
                createValidShippingAddress()
        );
    }
}
