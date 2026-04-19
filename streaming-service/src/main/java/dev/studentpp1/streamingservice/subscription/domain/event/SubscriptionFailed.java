package dev.studentpp1.streamingservice.subscription.domain.event;

import java.time.Instant;

public record SubscriptionFailed(
        Long userId,
        String userEmail,
        String planName,
        String reason,
        Instant occurredAt
) {}
