package dev.studentpp1.streamingservice.subscription.domain.model;

import java.math.BigDecimal;

public record CheckoutCommand(
        String productName,
        BigDecimal price,
        Long userId,
        String userEmail
) {
}
