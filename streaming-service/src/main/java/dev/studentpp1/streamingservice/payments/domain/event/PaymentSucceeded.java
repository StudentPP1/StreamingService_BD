package dev.studentpp1.streamingservice.payments.domain.event;

import java.time.Instant;

public record PaymentSucceeded(
        Long paymentId,
        Long userId,
        String userEmail,
        String planName,
        String providerSessionId,
        Instant occurredAt
) {
}

