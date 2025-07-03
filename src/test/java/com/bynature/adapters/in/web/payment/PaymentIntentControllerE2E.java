package com.bynature.adapters.in.web.payment;

import com.bynature.AbstractByNatureTest;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;

import java.util.UUID;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class PaymentIntentControllerE2E extends AbstractByNatureTest {

    @BeforeEach
    public void setUp() {
        // Authenticate before each test
        authenticateUser();
    }

    @Test
    public void whenCreatePaymentIntent_thenReturnSuccessfulResponse_E2E() {
        // Given
        UUID orderId = UUID.randomUUID();
        PaymentIntentRequest request = new PaymentIntentRequest(
                10L,                  // amount in euros
                "test@example.com",   // email
                "Test Product",       // productName
                orderId,              // orderId
                "customer123",        // customerId
                "+33612345678",       // phone
                "PENDING"             // state
        );

        // When
        ResponseEntity<String> response = restTemplate.exchange(
                "/create-payment-intent",
                HttpMethod.POST,
                createAuthenticatedEntity(request),
                String.class
        );

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();

        try {
            JSONObject jsonResponse = new JSONObject(response.getBody());

            // Verify essential payment intent fields
            assertThat(jsonResponse.has("id")).isTrue();
            assertThat(jsonResponse.has("object")).isTrue();
            assertThat(jsonResponse.getString("object")).isEqualTo("payment_intent");
            assertThat(jsonResponse.getLong("amount")).isEqualTo(1000); // 10€ converted to cents
            assertThat(jsonResponse.getString("currency")).isEqualTo("eur");

            // Verify metadata contains our request data
            JSONObject metadata = jsonResponse.getJSONObject("metadata");
            assertThat(metadata.getString("productName")).isEqualTo("Test Product");
            assertThat(metadata.getString("orderId")).isEqualTo(orderId.toString());
        } catch (JSONException e) {
            throw new AssertionError("Failed to parse JSON response: " + e.getMessage());
        }
    }

    private static Stream<Arguments> invalidRequestsProvider() {
        return Stream.of(
                Arguments.of(
                        "Invalid amount (too low)",
                        new PaymentIntentRequest(3L, "test@example.com", "Test Product",
                                UUID.randomUUID(), "customer123", "+33612345678", "PENDING")
                ),
                Arguments.of(
                        "Invalid email format",
                        new PaymentIntentRequest(10L, "not-an-email", "Test Product",
                                UUID.randomUUID(), "customer123", "+33612345678", "PENDING")
                ),
                Arguments.of(
                        "Empty product name",
                        new PaymentIntentRequest(10L, "test@example.com", "",
                                UUID.randomUUID(), "customer123", "+33612345678", "PENDING")
                ),
                Arguments.of(
                        "Empty customer ID",
                        new PaymentIntentRequest(10L, "test@example.com", "Test Product",
                                UUID.randomUUID(), "", "+33612345678", "PENDING")
                )
        );
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("invalidRequestsProvider")
    void whenCreatePaymentIntentWithInvalidData_thenReturnBadRequest_E2E(String testName, PaymentIntentRequest request) {
        // When
        ResponseEntity<String> response = restTemplate.exchange(
                "/create-payment-intent",
                HttpMethod.POST,
                createAuthenticatedEntity(request),
                String.class
        );

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void whenCreatePaymentIntentWithNullFields_thenReturnBadRequest_E2E() {
        // Given - Request with null fields that should trigger validation errors
        PaymentIntentRequest request = new PaymentIntentRequest(
                null,               // null amount
                "test@example.com",
                "Test Product",
                UUID.randomUUID(),
                "customer123",
                "+33612345678",
                "PENDING"
        );

        // When
        ResponseEntity<String> response = restTemplate.exchange(
                "/create-payment-intent",
                HttpMethod.POST,
                createAuthenticatedEntity(request),
                String.class
        );

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void whenCreatePaymentIntentWithExtremelyLargeAmount_thenHandleAppropriately_E2E() {
        // Given
        PaymentIntentRequest request = new PaymentIntentRequest(
                Long.MAX_VALUE,     // Extremely large amount
                "test@example.com",
                "Test Product",
                UUID.randomUUID(),
                "customer123",
                "+33612345678",
                "PENDING"
        );

        try {
            // When
            ResponseEntity<String> response = restTemplate.exchange(
                    "/create-payment-intent",
                    HttpMethod.POST,
                    createAuthenticatedEntity(request),
                    String.class
            );

            // Then - Most likely will fail due to Stripe limitations
            // We're just checking the application doesn't crash
            assertThat(response).isNotNull();
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        } catch (RestClientException e) {
            // Expected exception when Stripe rejects extremely large amounts
            assertThat(e.getMessage()).contains("error");
        }
    }
}