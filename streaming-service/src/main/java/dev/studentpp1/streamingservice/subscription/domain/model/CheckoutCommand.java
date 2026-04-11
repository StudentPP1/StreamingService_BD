package dev.studentpp1.streamingservice.subscription.domain.model;

import java.math.BigDecimal;
import java.util.Map;

public record CheckoutCommand(
        String productName,
        BigDecimal price,
        Long userId,
        Map<String, String> metadata
) {
}

