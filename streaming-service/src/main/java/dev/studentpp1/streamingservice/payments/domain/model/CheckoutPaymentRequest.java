package dev.studentpp1.streamingservice.payments.domain.model;

import java.math.BigDecimal;

public record CheckoutPaymentRequest(
        String productName,
        BigDecimal price,
        Long userId,
        String userEmail
) {
}
