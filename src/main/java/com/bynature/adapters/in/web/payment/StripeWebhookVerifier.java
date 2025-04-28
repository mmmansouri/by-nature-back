package com.bynature.adapters.in.web.payment;

import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.Event;

public interface StripeWebhookVerifier {
    Event verifyAndParseEvent(String payload, String signature, String secret)
            throws SignatureVerificationException;
}
