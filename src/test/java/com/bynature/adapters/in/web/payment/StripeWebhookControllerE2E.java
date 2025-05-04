package com.bynature.adapters.in.web.payment;

import com.bynature.AbstractByNatureTest;
import com.bynature.adapters.in.web.order.dto.request.OrderCreationRequest;
import com.bynature.adapters.in.web.order.dto.request.OrderItemCreationRequest;
import com.bynature.adapters.in.web.order.dto.request.ShippingAddressCreationRequest;
import com.bynature.adapters.in.web.order.dto.response.OrderRetrievalResponse;
import com.bynature.domain.model.OrderStatus;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.Event;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class StripeWebhookControllerE2E extends AbstractByNatureTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @MockitoBean
    private StripeWebhookVerifier webhookVerifier;

    private static final UUID VALID_CUSTOMER_ID = UUID.fromString("f47ac10b-58cc-4372-a567-0e02b2c3d479");
    private static final UUID VALID_ITEM_ID = UUID.fromString("4ad102fd-bf4a-439f-8027-5c3cf527ffaf");
    private static final int WAIT_MILLIS = 100;

    private UUID testOrderId;
    private String testPaymentIntentId;

    @BeforeEach
    void setup() {
        // Create a test order
        OrderCreationRequest orderRequest = createValidOrderRequest();
        ResponseEntity<OrderRetrievalResponse> response = restTemplate.postForEntity(
                "/orders",
                new HttpEntity<>(orderRequest, createJsonHeaders()),
                OrderRetrievalResponse.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        testOrderId = response.getBody().id();
        testPaymentIntentId = "pi_" + UUID.randomUUID().toString().replace("-", "");
    }

    @Test
    @DisplayName("When receiving payment_intent.created event, order status should be updated")
    void whenPaymentIntentCreatedEvent_thenUpdateOrderStatus_E2E() throws Exception {
        // Given
        String eventType = "payment_intent.created";
        String payload = createPaymentIntentEventPayload(eventType, testOrderId);
        String signature = createMockStripeSignature();

        Event mockEvent = createMockEvent(eventType, payload);
        when(webhookVerifier.verifyAndParseEvent(anyString(), anyString(), anyString()))
                .thenReturn(mockEvent);

        // When
        ResponseEntity<String> response = sendWebhookRequest(payload, signature);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        // Verify order status was updated after a short delay
        TimeUnit.MILLISECONDS.sleep(WAIT_MILLIS);
        OrderRetrievalResponse order = retrieveOrder(testOrderId);
        assertThat(order.status()).isEqualTo(OrderStatus.PAYMENT_INTEND_CREATED.toString());
    }

    @Test
    @DisplayName("When receiving payment_intent.succeeded event, order status should be updated")
    void whenPaymentIntentSucceededEvent_thenUpdateOrderStatus_E2E() throws Exception {
        // Given
        String eventType = "payment_intent.succeeded";
        String payload = createPaymentIntentEventPayload(eventType, testOrderId);
        String signature = createMockStripeSignature();

        Event mockEvent = createMockEvent(eventType, payload);
        when(webhookVerifier.verifyAndParseEvent(anyString(), anyString(), anyString()))
                .thenReturn(mockEvent);

        // When
        ResponseEntity<String> response = sendWebhookRequest(payload, signature);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        // Verify order status was updated
        TimeUnit.MILLISECONDS.sleep(WAIT_MILLIS);
        OrderRetrievalResponse order = retrieveOrder(testOrderId);
        assertThat(order.status()).isEqualTo(OrderStatus.PAYMENT_PROCESSING.toString());
    }

    @Test
    @DisplayName("When receiving charge.succeeded event, order status should be confirmed")
    void whenChargeSucceededEvent_thenConfirmOrderPayment_E2E() throws Exception {
        // Given
        String eventType = "charge.succeeded";
        String payload = createChargeEventPayload(eventType, testOrderId);
        String signature = createMockStripeSignature();

        Event mockEvent = createMockEvent(eventType, payload);
        when(webhookVerifier.verifyAndParseEvent(anyString(), anyString(), anyString()))
                .thenReturn(mockEvent);

        // When
        ResponseEntity<String> response = sendWebhookRequest(payload, signature);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        // Verify order status was updated
        TimeUnit.MILLISECONDS.sleep(WAIT_MILLIS);
        OrderRetrievalResponse order = retrieveOrder(testOrderId);
        assertThat(order.status()).isEqualTo(OrderStatus.PAYMENT_CONFIRMED.toString());
    }

    @Test
    @DisplayName("When receiving payment_intent.payment_failed event, order status should be failed")
    void whenPaymentIntentFailedEvent_thenFailOrderPayment_E2E() throws Exception {
        // Given
        String eventType = "payment_intent.payment_failed";
        String payload = createPaymentIntentEventPayload(eventType, testOrderId);
        String signature = createMockStripeSignature();

        Event mockEvent = createMockEvent(eventType, payload);
        when(webhookVerifier.verifyAndParseEvent(anyString(), anyString(), anyString()))
                .thenReturn(mockEvent);

        // When
        ResponseEntity<String> response = sendWebhookRequest(payload, signature);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        // Verify order status was updated
        TimeUnit.MILLISECONDS.sleep(WAIT_MILLIS);
        OrderRetrievalResponse order = retrieveOrder(testOrderId);
        assertThat(order.status()).isEqualTo(OrderStatus.PAYMENT_FAILED.toString());
    }

    @Test
    @DisplayName("When receiving charge.failed event, order status should be failed")
    void whenChargeFailedEvent_thenFailOrderPayment_E2E() throws Exception {
        // Given
        String eventType = "charge.failed";
        String payload = createChargeEventPayload(eventType, testOrderId);
        String signature = createMockStripeSignature();

        Event mockEvent = createMockEvent(eventType, payload);
        when(webhookVerifier.verifyAndParseEvent(anyString(), anyString(), anyString()))
                .thenReturn(mockEvent);

        // When
        ResponseEntity<String> response = sendWebhookRequest(payload, signature);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        // Verify order status was updated
        TimeUnit.MILLISECONDS.sleep(WAIT_MILLIS);
        OrderRetrievalResponse order = retrieveOrder(testOrderId);
        assertThat(order.status()).isEqualTo(OrderStatus.PAYMENT_FAILED.toString());
    }

    @Test
    @DisplayName("When webhook signature is invalid, should return 400 Bad Request")
    void whenInvalidSignature_thenReturnBadRequest_E2E() throws Exception {
        // Given
        String eventType = "payment_intent.created";
        String payload = createPaymentIntentEventPayload(eventType, testOrderId);
        String invalidSignature = createMockStripeSignature();

        when(webhookVerifier.verifyAndParseEvent(anyString(), anyString(), anyString()))
                .thenThrow(new SignatureVerificationException("Invalid signature", invalidSignature));

        // When
        ResponseEntity<String> response = sendWebhookRequest(payload, invalidSignature);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).contains("Signature invalide");
    }

    @Test
    @DisplayName("When event type is not handled, should return bad request")
    void whenUnhandledEventType_thenReturnBadRequest_E2E() throws Exception {
        // Given
        String eventType = "checkout.session.completed";
        String payload = createGenericEventPayload(eventType);
        String signature = createMockStripeSignature();

        Event mockEvent = createMockEvent(eventType, payload);
        when(webhookVerifier.verifyAndParseEvent(anyString(), anyString(), anyString()))
                .thenReturn(mockEvent);

        // When
        ResponseEntity<String> response = sendWebhookRequest(payload, signature);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    @DisplayName("When deserializer returns empty object, should return bad request")
    void whenDeserializerReturnsEmptyObject_thenReturnBadRequest_E2E() throws Exception {
        // Given
        String eventType = "payment_intent.created";
        String payload = createPaymentIntentEventPayload(eventType, testOrderId);
        String signature = createMockStripeSignature();

        // Create an event with empty deserializer result
        Event mockEvent = createEmptyObjectEvent(eventType, payload);
        when(webhookVerifier.verifyAndParseEvent(anyString(), anyString(), anyString()))
                .thenReturn(mockEvent);

        // When
        ResponseEntity<String> response = sendWebhookRequest(payload, signature);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).contains("Failed to deserialize Stripe object");
    }

    // Helper methods
    private String createMockStripeSignature() {
        long timestamp = System.currentTimeMillis() / 1000;
        return "t=" + timestamp + ",v1=mock_signature_value";
    }

    private ResponseEntity<String> sendWebhookRequest(String payload, String signature) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("Stripe-Signature", signature);

        HttpEntity<String> request = new HttpEntity<>(payload, headers);
        return restTemplate.postForEntity("/webhook/stripe", request, String.class);
    }

    private OrderRetrievalResponse retrieveOrder(UUID orderId) {
        return restTemplate.getForEntity(
                "/orders/" + orderId,
                OrderRetrievalResponse.class
        ).getBody();
    }

    private String createPaymentIntentEventPayload(String eventType, UUID orderId) throws JSONException {
        JSONObject metadata = new JSONObject();
        metadata.put("orderId", orderId.toString());

        JSONObject paymentIntent = new JSONObject();
        paymentIntent.put("id", testPaymentIntentId);
        paymentIntent.put("object", "payment_intent");
        paymentIntent.put("client_secret", testPaymentIntentId + "_secret");
        paymentIntent.put("metadata", metadata);

        JSONObject data = new JSONObject();
        data.put("object", paymentIntent);

        JSONObject event = new JSONObject();
        event.put("id", "evt_" + UUID.randomUUID().toString().replace("-", ""));
        event.put("object", "event");
        event.put("type", eventType);
        event.put("data", data);

        return event.toString();
    }

    private String createChargeEventPayload(String eventType, UUID orderId) throws JSONException {
        JSONObject metadata = new JSONObject();
        metadata.put("orderId", orderId.toString());

        JSONObject charge = new JSONObject();
        charge.put("id", "ch_" + UUID.randomUUID().toString().replace("-", ""));
        charge.put("object", "charge");
        charge.put("metadata", metadata);

        JSONObject data = new JSONObject();
        data.put("object", charge);

        JSONObject event = new JSONObject();
        event.put("id", "evt_" + UUID.randomUUID().toString().replace("-", ""));
        event.put("object", "event");
        event.put("type", eventType);
        event.put("data", data);

        return event.toString();
    }

    private String createGenericEventPayload(String eventType) throws JSONException {
        JSONObject event = new JSONObject();
        event.put("id", "evt_" + UUID.randomUUID().toString().replace("-", ""));
        event.put("object", "event");
        event.put("type", eventType);

        return event.toString();
    }

    private Event createMockEvent(String eventType, String rawJson) {
        Event event = mock(Event.class);
        when(event.getType()).thenReturn(eventType);

        // Mock the data deserializer
        com.stripe.model.EventDataObjectDeserializer deserializer = mock(com.stripe.model.EventDataObjectDeserializer.class);
        when(event.getDataObjectDeserializer()).thenReturn(deserializer);

        // Different behavior based on event type
        if (eventType.startsWith("payment_intent")) {
            com.stripe.model.PaymentIntent paymentIntent = mock(com.stripe.model.PaymentIntent.class);
            when(deserializer.getObject()).thenReturn(Optional.of(paymentIntent));

            // Mock metadata
            Map<String, String> metadata = new HashMap<>();
            metadata.put("orderId", testOrderId.toString());
            when(paymentIntent.getMetadata()).thenReturn(metadata);
            when(paymentIntent.getClientSecret()).thenReturn(testPaymentIntentId + "_secret");
            when(paymentIntent.getId()).thenReturn(testPaymentIntentId);
        } else if (eventType.startsWith("charge")) {
            com.stripe.model.Charge charge = mock(com.stripe.model.Charge.class);
            when(deserializer.getObject()).thenReturn(Optional.of(charge));

            // Mock metadata
            Map<String, String> metadata = new HashMap<>();
            metadata.put("orderId", testOrderId.toString());
            when(charge.getMetadata()).thenReturn(metadata);
            when(charge.getId()).thenReturn("ch_test");
        } else {
            when(deserializer.getObject()).thenReturn(Optional.empty());
            when(deserializer.getRawJson()).thenReturn(rawJson);
        }

        return event;
    }

    private Event createEmptyObjectEvent(String eventType, String rawJson) {
        Event event = mock(Event.class);
        when(event.getType()).thenReturn(eventType);

        // Create deserializer that returns empty
        com.stripe.model.EventDataObjectDeserializer deserializer = mock(com.stripe.model.EventDataObjectDeserializer.class);
        when(event.getDataObjectDeserializer()).thenReturn(deserializer);
        when(deserializer.getObject()).thenReturn(Optional.empty());
        when(deserializer.getRawJson()).thenReturn(rawJson);

        return event;
    }

    private static HttpHeaders createJsonHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }

    private static ShippingAddressCreationRequest createValidShippingAddress() {
        return new ShippingAddressCreationRequest(VALID_CUSTOMER_ID, "My Address",
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