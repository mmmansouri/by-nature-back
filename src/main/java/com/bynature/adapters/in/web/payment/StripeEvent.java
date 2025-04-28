package com.bynature.adapters.in.web.payment;

public enum StripeEvent {
    PAYMENT_INTENT_CREATED("payment_intent.created"),
    PAYMENT_INTENT_SUCCEEDED("payment_intent.succeeded"),
    PAYMENT_INTENT_PAYMENT_FAILED("payment_intent.payment_failed"),
    CHARGE_SUCCEEDED("charge.succeeded"),
    CHARGE_FAILED("charge.failed"),
    CHARGE_UPDATED("charge.updated");

    private final String eventType;

    StripeEvent(String eventType) {
        this.eventType = eventType;
    }

    public String getEventType() {
        return eventType;
    }

    public static StripeEvent fromString(String eventType) {
        for (StripeEvent event : StripeEvent.values()) {
            if (event.eventType.equals(eventType)) {
                return event;
            }
        }
        throw new IllegalArgumentException("Unknown event type: " + eventType);
    }
}
