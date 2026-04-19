package dev.studentpp1.streamingservice.subscription.domain.event;

import java.time.Instant;

public record SubscriptionLinkedToPayment(
        Long paymentId,
        String providerSessionId,
        Long subscriptionId,
        Instant occurredAt
) {
}

