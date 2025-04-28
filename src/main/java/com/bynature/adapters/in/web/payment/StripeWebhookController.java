package com.bynature.adapters.in.web.payment;

import com.bynature.domain.model.OrderStatus;
import com.bynature.domain.service.OrderService;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.Charge;
import com.stripe.model.Event;
import com.stripe.model.EventDataObjectDeserializer;
import com.stripe.model.PaymentIntent;
import com.stripe.model.StripeObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

@RestController
public class StripeWebhookController {
    private static final Logger logger = Logger.getLogger(StripeWebhookController.class.getName());

    @Value("${stripe.webhook.secret}")
    private String endpointSecret;

    private final OrderService orderService;
    private final StripeWebhookVerifier webhookVerifier;

    public StripeWebhookController(OrderService orderService, StripeWebhookVerifier webhookVerifier) {
        this.orderService = orderService;
        this.webhookVerifier = webhookVerifier;
    }

    @PostMapping("/webhook/stripe")
    public ResponseEntity<String> handleStripeWebhook(
            @RequestBody String payload,
            @RequestHeader("Stripe-Signature") String sigHeader) {

        Event event;

        try {
            event = webhookVerifier.verifyAndParseEvent(payload, sigHeader, endpointSecret);
        } catch (SignatureVerificationException e) {
            logger.warning("Signature invalide: " + e.getMessage());
            return ResponseEntity.badRequest().body("Signature invalide");
        }

        EventDataObjectDeserializer dataObjectDeserializer = event.getDataObjectDeserializer();
        StripeObject stripeObject;

        if (dataObjectDeserializer.getObject().isPresent()) {
            stripeObject = dataObjectDeserializer.getObject().get();
            logger.info("Event received: " + event.getType());
            switch (StripeEvent.fromString(event.getType())) {
                case PAYMENT_INTENT_CREATED:
                    handlePaymentIntentCreated(stripeObject);
                    break;
                case PAYMENT_INTENT_SUCCEEDED:
                    handleSuccessfulPayment(stripeObject);
                    break;
                case CHARGE_SUCCEEDED:
                    handleSuccessfulCharge(stripeObject);
                    break;
                case PAYMENT_INTENT_PAYMENT_FAILED:
                    handleFailedPayment(stripeObject);
                    break;
                case CHARGE_FAILED:
                    handleChargeFailed(stripeObject);
                    break;
                default:
                    logger.info("Événement non géré: " + event.getType());
            }

            return ResponseEntity.ok("Événement reçu");
        } else {
            logger.warning("Failed to deserialize Stripe object : " + dataObjectDeserializer.getRawJson());
            return ResponseEntity.badRequest().body("Failed to deserialize Stripe object: " + dataObjectDeserializer.getRawJson());
        }
    }

    private static PaymentIntent getPaymentIntent(StripeObject stripeObject) {
        PaymentIntent paymentIntent = (PaymentIntent) stripeObject;
        assert paymentIntent != null;
        return paymentIntent;
    }

    private static Charge getCharge(StripeObject stripeObject) {
        Charge charge = (Charge) stripeObject;
        assert charge != null;
        return charge;
    }

    private void handlePaymentIntentCreated(StripeObject paymentIntentObject) {
        PaymentIntent paymentIntent = getPaymentIntent(paymentIntentObject);
        String orderId = paymentIntent.getMetadata().get("orderId");
        String paymentIntentId = paymentIntent.getClientSecret();
        orderService.updateOrderStatus(UUID.fromString(orderId), OrderStatus.PAYMENT_INTEND_CREATED, paymentIntentId);

        logger.log(Level.INFO, "Payment intent created: {0}, orderId: {1}",
                new Object[]{paymentIntent.getId(), orderId});
    }

    private void handleSuccessfulPayment(StripeObject paymentIntentObject) {
        PaymentIntent paymentIntent = getPaymentIntent(paymentIntentObject);
        String orderId = paymentIntent.getMetadata().get("orderId");
        orderService.updateOrderStatus(UUID.fromString(orderId), OrderStatus.PAYMENT_PROCESSING);

        logger.log(Level.INFO, "Payment processing: {0}, orderId: {1}",
                new Object[]{paymentIntent.getId(), orderId});
    }

    private void handleSuccessfulCharge(StripeObject paymentIntentObject) {
        Charge paymentCharge = getCharge(paymentIntentObject);
        String orderId = paymentCharge.getMetadata().get("orderId");
        orderService.updateOrderStatus(UUID.fromString(orderId), OrderStatus.PAYMENT_CONFIRMED);

        logger.log(Level.INFO, "Payment confirmed: {0}, orderId: {1}",
                new Object[]{paymentCharge.getId(), orderId});
    }

    private void handleFailedPayment(StripeObject paymentIntentObject) {
        PaymentIntent paymentIntent = getPaymentIntent(paymentIntentObject);
        String orderId = paymentIntent.getMetadata().get("orderId");
        orderService.updateOrderStatus(UUID.fromString(orderId), OrderStatus.PAYMENT_FAILED);

        logger.log(Level.INFO, "Payment failed: {0}, orderId: {1}",
                new Object[]{paymentIntent.getId(), orderId});
    }

    private void handleChargeFailed(StripeObject paymentIntentObject) {
        Charge paymentCharge = getCharge(paymentIntentObject);
        String orderId = paymentCharge.getMetadata().get("orderId");
        orderService.updateOrderStatus(UUID.fromString(orderId), OrderStatus.PAYMENT_FAILED);

        logger.log(Level.INFO, "Charge failed: {0}, orderId: {1}",
                new Object[]{paymentCharge.getId(), orderId});
    }
}
