package dev.studentpp1.streamingservice.subscription.domain.event;

import java.time.Instant;
import java.time.LocalDateTime;

public record SubscriptionActivated(
        Long subscriptionId,
        Long userId,
        String userEmail,
        String planName,
        LocalDateTime expiresAt,
        Instant occurredAt
) {}
