package com.bynature.adapters.in.web.payment;

import com.bynature.domain.model.OrderStatus;
import com.bynature.domain.service.OrderService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.Charge;
import com.stripe.model.Event;
import com.stripe.model.PaymentIntent;
import com.stripe.model.StripeObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;
import java.util.UUID;

/**
 * Modern Stripe Webhook Controller
 * Uses Java 23+ features: pattern matching, switch expressions, records
 * SLF4J for logging with Spring Boot best practices
 */
@RestController
public class StripeWebhookController {
    private static final Logger log = LoggerFactory.getLogger(StripeWebhookController.class);

    private final String webhookSecret;
    private final OrderService orderService;
    private final StripeWebhookVerifier webhookVerifier;
    private final ObjectMapper objectMapper;

    public StripeWebhookController(
            @Value("${stripe.webhook.secret}") String webhookSecret,
            OrderService orderService,
            StripeWebhookVerifier webhookVerifier,
            ObjectMapper objectMapper) {
        this.webhookSecret = webhookSecret;
        this.orderService = orderService;
        this.webhookVerifier = webhookVerifier;
        this.objectMapper = objectMapper;
    }

    @PostMapping("/webhook/stripe")
    public ResponseEntity<String> handleStripeWebhook(
            @RequestBody String payload,
            @RequestHeader("Stripe-Signature") String sigHeader) {

        log.info("🔔 Webhook received! Payload: {} bytes", payload.length());
        log.debug("🔑 Signature: {}...", sigHeader.substring(0, Math.min(50, sigHeader.length())));

        // Verify webhook signature
        Event event = verifyWebhookSignature(payload, sigHeader)
                .orElseGet(() -> null);

        if (event == null) {
            return ResponseEntity.badRequest().body("Invalid signature");
        }

        log.info("✅ Webhook verified: {}", event.getType());

        // Process event with fallback to raw JSON parsing
        return processEvent(event);
    }

    private Optional<Event> verifyWebhookSignature(String payload, String signature) {
        try {
            Event event = webhookVerifier.verifyAndParseEvent(payload, signature, webhookSecret);
            return Optional.of(event);
        } catch (SignatureVerificationException e) {
            log.warn("❌ Signature verification failed: {}", e.getMessage());
            return Optional.empty();
        }
    }

    private ResponseEntity<String> processEvent(Event event) {
        return event.getDataObjectDeserializer()
                .getObject()
                .map(obj -> processDeserializedEvent(event.getType(), obj))
                .orElseGet(() -> processRawJsonEvent(event));
    }

    private ResponseEntity<String> processDeserializedEvent(String eventType, StripeObject stripeObject) {
        try {
            return switch (StripeEvent.fromString(eventType)) {
                case PAYMENT_INTENT_CREATED -> handlePaymentIntentCreated(stripeObject);
                case PAYMENT_INTENT_SUCCEEDED -> handlePaymentIntentSucceeded(stripeObject);
                case CHARGE_SUCCEEDED -> handleChargeSucceeded(stripeObject);
                case PAYMENT_INTENT_PAYMENT_FAILED -> handlePaymentIntentFailed(stripeObject);
                case CHARGE_FAILED -> handleChargeFailed(stripeObject);
                default -> {
                    log.info("ℹ️  Unhandled event type: {}", eventType);
                    yield ResponseEntity.ok("Event acknowledged");
                }
            };
        } catch (Exception e) {
            log.error("❌ Error processing event: {}", eventType, e);
            return ResponseEntity.internalServerError()
                    .body("Error: " + e.getMessage());
        }
    }

    private ResponseEntity<String> processRawJsonEvent(Event event) {
        String rawJson = event.getDataObjectDeserializer().getRawJson();
        log.warn("⚠️  Deserialization issue, using raw JSON (length: {} chars)", rawJson.length());

        try {
            JsonNode data = objectMapper.readTree(rawJson);
            return extractOrderIdFromMetadata(data)
                    .map(orderId -> processRawEvent(event.getType(), orderId, data))
                    .orElseGet(() -> {
                        log.warn("⚠️  No orderId in metadata for {}", event.getType());
                        return ResponseEntity.ok("Event acknowledged (no orderId)");
                    });
        } catch (Exception e) {
            log.error("❌ Error processing raw JSON event", e);
            return ResponseEntity.internalServerError()
                    .body("Error: " + e.getMessage());
        }
    }

    private Optional<UUID> extractOrderIdFromMetadata(JsonNode data) {
        return Optional.ofNullable(data.path("metadata").path("orderId").asText(null))
                .map(UUID::fromString);
    }

    private ResponseEntity<String> processRawEvent(String eventType, UUID orderId, JsonNode data) {
        log.info("✅ Processing {} for orderId: {}", eventType, orderId);

        return switch (StripeEvent.fromString(eventType)) {
            case PAYMENT_INTENT_CREATED -> {
                String clientSecret = data.path("client_secret").asText(null);
                orderService.updateOrderStatus(orderId, OrderStatus.PAYMENT_INTEND_CREATED, clientSecret);
                yield ResponseEntity.ok("Payment intent created");
            }
            case PAYMENT_INTENT_SUCCEEDED -> {
                orderService.updateOrderStatus(orderId, OrderStatus.PAYMENT_PROCESSING);
                yield ResponseEntity.ok("Payment processing");
            }
            case CHARGE_SUCCEEDED -> {
                orderService.updateOrderStatus(orderId, OrderStatus.PAYMENT_CONFIRMED);
                yield ResponseEntity.ok("Payment confirmed");
            }
            case PAYMENT_INTENT_PAYMENT_FAILED, CHARGE_FAILED -> {
                orderService.updateOrderStatus(orderId, OrderStatus.PAYMENT_FAILED);
                yield ResponseEntity.ok("Payment failed");
            }
            default -> {
                log.info("ℹ️  Unhandled event type: {}", eventType);
                yield ResponseEntity.ok("Event acknowledged");
            }
        };
    }

    // Modern handler methods using pattern matching and clean code

    private ResponseEntity<String> handlePaymentIntentCreated(StripeObject stripeObject) {
        if (stripeObject instanceof PaymentIntent paymentIntent) {
            String orderId = paymentIntent.getMetadata().get("orderId");
            String clientSecret = paymentIntent.getClientSecret();

            orderService.updateOrderStatus(
                    UUID.fromString(orderId),
                    OrderStatus.PAYMENT_INTEND_CREATED,
                    clientSecret
            );

            log.info("Payment intent created: {}, orderId: {}", paymentIntent.getId(), orderId);
            return ResponseEntity.ok("Payment intent created");
        }
        return ResponseEntity.badRequest().body("Invalid payment intent object");
    }

    private ResponseEntity<String> handlePaymentIntentSucceeded(StripeObject stripeObject) {
        if (stripeObject instanceof PaymentIntent paymentIntent) {
            String orderId = paymentIntent.getMetadata().get("orderId");

            orderService.updateOrderStatus(
                    UUID.fromString(orderId),
                    OrderStatus.PAYMENT_PROCESSING
            );

            log.info("Payment processing: {}, orderId: {}", paymentIntent.getId(), orderId);
            return ResponseEntity.ok("Payment processing");
        }
        return ResponseEntity.badRequest().body("Invalid payment intent object");
    }

    private ResponseEntity<String> handleChargeSucceeded(StripeObject stripeObject) {
        if (stripeObject instanceof Charge charge) {
            String orderId = charge.getMetadata().get("orderId");

            orderService.updateOrderStatus(
                    UUID.fromString(orderId),
                    OrderStatus.PAYMENT_CONFIRMED
            );

            log.info("Payment confirmed: {}, orderId: {}", charge.getId(), orderId);
            return ResponseEntity.ok("Payment confirmed");
        }
        return ResponseEntity.badRequest().body("Invalid charge object");
    }

    private ResponseEntity<String> handlePaymentIntentFailed(StripeObject stripeObject) {
        if (stripeObject instanceof PaymentIntent paymentIntent) {
            String orderId = paymentIntent.getMetadata().get("orderId");

            orderService.updateOrderStatus(
                    UUID.fromString(orderId),
                    OrderStatus.PAYMENT_FAILED
            );

            log.info("Payment failed: {}, orderId: {}", paymentIntent.getId(), orderId);
            return ResponseEntity.ok("Payment failed");
        }
        return ResponseEntity.badRequest().body("Invalid payment intent object");
    }

    private ResponseEntity<String> handleChargeFailed(StripeObject stripeObject) {
        if (stripeObject instanceof Charge charge) {
            String orderId = charge.getMetadata().get("orderId");

            orderService.updateOrderStatus(
                    UUID.fromString(orderId),
                    OrderStatus.PAYMENT_FAILED
            );

            log.info("Charge failed: {}, orderId: {}", charge.getId(), orderId);
            return ResponseEntity.ok("Charge failed");
        }
        return ResponseEntity.badRequest().body("Invalid charge object");
    }
}
