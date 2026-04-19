package dev.studentpp1.streamingservice.subscription.api.event;

import java.time.Instant;

public record SubscriptionLinkedToPaymentEvent(
        Long paymentId,
        String providerSessionId,
        Long subscriptionId,
        Instant occurredAt
) {
}

