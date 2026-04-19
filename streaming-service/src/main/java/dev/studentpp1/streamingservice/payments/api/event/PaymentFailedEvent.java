package dev.studentpp1.streamingservice.payments.api.event;

import java.math.BigDecimal;
import java.time.Instant;

public record PaymentFailedEvent(
        Long paymentId,
        Long userId,
        String userEmail,
        String planName,
        String providerSessionId,
        Long userSubscriptionId,
        String reason,
        BigDecimal amount,
        String currency,
        Instant occurredAt
) {
}

