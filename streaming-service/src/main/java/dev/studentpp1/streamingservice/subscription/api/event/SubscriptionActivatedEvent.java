package dev.studentpp1.streamingservice.subscription.api.event;

import java.time.Instant;
import java.time.LocalDateTime;

public record SubscriptionActivatedEvent(
        Long subscriptionId,
        Long userId,
        String userEmail,
        String planName,
        LocalDateTime expiresAt,
        Instant occurredAt
) {
}

