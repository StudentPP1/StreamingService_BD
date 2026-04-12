package dev.studentpp1.streamingservice.payments.domain.model;

import java.math.BigDecimal;
import java.util.Map;

public record CheckoutPaymentRequest(
        String productName,
        BigDecimal price,
        Long userId,
        Map<String, String> metadata
) {
}

