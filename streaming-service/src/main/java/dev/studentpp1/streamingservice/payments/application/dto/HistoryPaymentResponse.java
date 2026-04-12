package dev.studentpp1.streamingservice.payments.application.dto;

import dev.studentpp1.streamingservice.payments.domain.model.PaymentStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record HistoryPaymentResponse(
        PaymentStatus status,
        LocalDateTime paidAt,
        BigDecimal amount,
        String subscriptionName
) {
}
