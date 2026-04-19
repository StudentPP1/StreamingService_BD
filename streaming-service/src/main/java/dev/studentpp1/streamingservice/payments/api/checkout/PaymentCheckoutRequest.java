package dev.studentpp1.streamingservice.payments.api.checkout;

import java.math.BigDecimal;

public record PaymentCheckoutRequest(
        String productName,
        BigDecimal price,
        Long userId,
        String userEmail
) {
}

