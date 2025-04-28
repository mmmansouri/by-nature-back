package com.bynature.adapters.in.web.payment;

import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.Event;
import com.stripe.net.Webhook;
import org.springframework.stereotype.Service;

@Service
public class DefaultStripeWebhookVerifier implements StripeWebhookVerifier {
    @Override
    public Event verifyAndParseEvent(String payload, String signature, String secret)
            throws SignatureVerificationException {
        return Webhook.constructEvent(payload, signature, secret);
    }
}