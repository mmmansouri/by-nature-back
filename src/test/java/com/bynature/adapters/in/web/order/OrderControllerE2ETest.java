package com.bynature.adapters.in.web.order;

import com.bynature.AbstractByNatureTest;
import com.bynature.adapters.in.web.order.dto.request.OrderCreationRequest;
import com.bynature.adapters.in.web.order.dto.request.OrderItemCreationRequest;
import com.bynature.adapters.in.web.order.dto.request.ShippingAddressCreationRequest;
import com.bynature.adapters.in.web.order.dto.response.OrderRetrievalResponse;
import com.bynature.domain.model.OrderStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;

import java.net.URI;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class OrderControllerE2ETest extends AbstractByNatureTest {

    // Valid UUID constants for testing
    private static final UUID VALID_CUSTOMER_ID = UUID.fromString("f47ac10b-58cc-4372-a567-0e02b2c3d479");
    private static final UUID VALID_ITEM_ID = UUID.fromString("4ad102fd-bf4a-439f-8027-5c3cf527ffaf");

    @BeforeEach
    public void setUp() {
        // Authenticate before each test
        authenticateUser();
    }

    @Test
    public void whenCreateOrder_shouldRetrieveIT_E2E() {
        // Prepare a sample ShippingAddressRequest
        ShippingAddressCreationRequest addressRequest = new ShippingAddressCreationRequest(
                VALID_CUSTOMER_ID,
                "My Address",
                "Mohamed", "Mohamed",
                "+33634164387",
                "toto@gmail.com",
                "123", "Avenue de la redoute",
                "Asnières", "Haut de France",
                "92600", "France");

        // Prepare a sample OrderRequest
        OrderCreationRequest orderCreationRequest = new OrderCreationRequest(VALID_CUSTOMER_ID,
                List.of(new OrderItemCreationRequest(VALID_ITEM_ID, 2)),
                100.0,
                addressRequest);

        // Execute the POST request using authenticated entity
        ResponseEntity<OrderRetrievalResponse> responseOrder = restTemplate.exchange(
                "/orders",
                HttpMethod.POST,
                createAuthenticatedEntity(orderCreationRequest),
                OrderRetrievalResponse.class
        );

        // Assert that we receive a 201 Created status
        assertThat(responseOrder.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        // Verify that the body contains the expected order data
        OrderRetrievalResponse orderResponse = responseOrder.getBody();
        assertThat(orderResponse).isNotNull();

        // Verify the Location header is set correctly
        URI location = responseOrder.getHeaders().getLocation();
        assertThat(location).isNotNull();

        // Call the GET /orders/{id} endpoint using authenticated entity
        ResponseEntity<OrderRetrievalResponse> getResponse = restTemplate.exchange(
                "/orders/{id}",
                HttpMethod.GET,
                createAuthenticatedEntity(),
                OrderRetrievalResponse.class,
                orderResponse.id()
        );

        // Assert the getResponse with same level of verification
        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        OrderRetrievalResponse orderRetrievalResponse = getResponse.getBody();
        assertThat(orderRetrievalResponse).isNotNull();
        assertThat(orderRetrievalResponse.id()).isEqualTo(orderResponse.id());
        assertThat(orderRetrievalResponse.customerId()).isEqualTo(orderCreationRequest.customerId());
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
        // Use exchange with authenticated entity
        ResponseEntity<OrderRetrievalResponse> response = restTemplate.exchange(
                "/orders/{id}",
                HttpMethod.GET,
                createAuthenticatedEntity(),
                OrderRetrievalResponse.class,
                UUID.randomUUID()
        );

        // Verify response
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void whenFetchOrdersByCustomer_thenReturnMatchingOrders_E2E() {
        // First create an order for the customer
        OrderCreationRequest orderRequest = createValidOrderRequest();

        ResponseEntity<OrderRetrievalResponse> createResponse = restTemplate.exchange(
                "/orders",
                HttpMethod.POST,
                createAuthenticatedEntity(orderRequest),
                OrderRetrievalResponse.class
        );

        assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        // Now fetch orders for this customer using authenticated entity
        ResponseEntity<List<OrderRetrievalResponse>> response = restTemplate.exchange(
                "/orders/customer/{id}",
                HttpMethod.GET,
                createAuthenticatedEntity(),
                new ParameterizedTypeReference<>() {},
                VALID_CUSTOMER_ID
        );

        // Verify response
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).isNotEmpty();

        // Verify that all orders belong to the specified customer
        response.getBody().forEach(order -> {
            assertThat(order.customerId()).isEqualTo(VALID_CUSTOMER_ID);
        });
    }

    private static Stream<Arguments> invalidOrderRequests() {
        return Stream.of(
                // Case: Null customer ID
                Arguments.of(
                        "Null customer ID",
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
                // Case: Negative total amount
                Arguments.of(
                        "Negative total amount",
                        new OrderCreationRequest(
                                VALID_CUSTOMER_ID,
                                List.of(new OrderItemCreationRequest(VALID_ITEM_ID, 2)),
                                -100.0,
                                createValidShippingAddress()
                        )
                ),
                // Case: Null shipping address
                Arguments.of(
                        "Null shipping address",
                        new OrderCreationRequest(
                                VALID_CUSTOMER_ID,
                                List.of(new OrderItemCreationRequest(VALID_ITEM_ID, 2)),
                                100.0,
                                null
                        )
                )
        );
    }

    @ParameterizedTest(name = "Invalid order validation: {0}")
    @MethodSource("invalidOrderRequests")
    void whenCreateInvalidOrder_thenReturnBadRequest_E2E(String testName, OrderCreationRequest invalidRequest) {
        // Use exchange with authenticated entity containing invalid request body
        ResponseEntity<ProblemDetail> response = restTemplate.exchange(
                "/orders",
                HttpMethod.POST,
                createAuthenticatedEntity(invalidRequest),
                ProblemDetail.class
        );

        // Verify HTTP 400 Bad Request status
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
    }

    // Helper methods
    private static ShippingAddressCreationRequest createValidShippingAddress() {
        return new ShippingAddressCreationRequest(
                VALID_CUSTOMER_ID,
                "My Address",
                "John", "Doe",
                "+33612345678",
                "john.doe@example.com",
                "123", "Main Street",
                "Paris", "Île-de-France",
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