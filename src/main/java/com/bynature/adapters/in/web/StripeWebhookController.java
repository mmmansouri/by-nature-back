package com.bynature.adapters.in.web;

import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.Event;
import com.stripe.model.EventDataObjectDeserializer;
import com.stripe.model.PaymentIntent;
import com.stripe.model.StripeObject;
import com.stripe.net.Webhook;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import java.util.logging.Logger;

@RestController
public class StripeWebhookController {
    private static final Logger logger = Logger.getLogger(StripeWebhookController.class.getName());

    @Value("${stripe.webhook.secret}")
    private String endpointSecret;

    @PostMapping("/webhook/stripe")
    public ResponseEntity<String> handleStripeWebhook(
            @RequestBody String payload,
            @RequestHeader("Stripe-Signature") String sigHeader) {

        Event event;

        try {
            event = Webhook.constructEvent(payload, sigHeader, endpointSecret);
        } catch (SignatureVerificationException e) {
            logger.warning("Signature invalide: " + e.getMessage());
            return ResponseEntity.badRequest().body("Signature invalide");
        }

        // Traiter l'événement
        EventDataObjectDeserializer dataObjectDeserializer = event.getDataObjectDeserializer();
        StripeObject stripeObject = null;

        if (dataObjectDeserializer.getObject().isPresent()) {
            stripeObject = dataObjectDeserializer.getObject().get();
        }

        switch (event.getType()) {
            case "payment_intent.succeeded":
                PaymentIntent paymentIntent = (PaymentIntent) stripeObject;
                logger.info("Paiement réussi: " + paymentIntent.getId());
                handleSuccessfulPayment(paymentIntent);
                break;
            case "payment_intent.payment_failed":
                PaymentIntent failedPaymentIntent = (PaymentIntent) stripeObject;
                logger.warning("Paiement échoué: " + failedPaymentIntent.getId());
                handleFailedPayment(failedPaymentIntent);
                break;
            default:
                logger.info("Événement non géré: " + event.getType());
        }

        return ResponseEntity.ok("Événement reçu");
    }

    private void handleSuccessfulPayment(PaymentIntent paymentIntent) {
        // Logique pour mettre à jour votre base de données, confirmer une commande, etc.
        String orderId = paymentIntent.getMetadata().get("orderId");
        // Par exemple: orderService.confirmPayment(orderId);
    }

    private void handleFailedPayment(PaymentIntent paymentIntent) {
        // Logique pour gérer l'échec du paiement
        String orderId = paymentIntent.getMetadata().get("orderId");
        // Par exemple: orderService.markPaymentFailed(orderId);
    }

}
