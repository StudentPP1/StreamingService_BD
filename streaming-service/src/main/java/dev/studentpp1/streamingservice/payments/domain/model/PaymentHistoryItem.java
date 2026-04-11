package dev.studentpp1.streamingservice.payments.domain.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PaymentHistoryItem(
        PaymentStatus status,
        LocalDateTime paidAt,
        BigDecimal amount,
        String subscriptionName
) {
}

