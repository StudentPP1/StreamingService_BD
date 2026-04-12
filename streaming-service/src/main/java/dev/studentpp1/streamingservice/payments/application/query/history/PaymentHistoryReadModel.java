package dev.studentpp1.streamingservice.payments.application.query.history;

import dev.studentpp1.streamingservice.payments.domain.model.PaymentStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PaymentHistoryReadModel(
        PaymentStatus status,
        LocalDateTime paidAt,
        BigDecimal amount,
        String subscriptionName
) {
}

