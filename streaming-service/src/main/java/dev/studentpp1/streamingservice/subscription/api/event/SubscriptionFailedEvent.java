package dev.studentpp1.streamingservice.subscription.api.event;

import java.time.Instant;

public record SubscriptionFailedEvent(
        Long userId,
        String userEmail,
        String planName,
        String reason,
        Instant occurredAt
) {
}

