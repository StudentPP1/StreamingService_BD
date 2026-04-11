package dev.studentpp1.streamingservice.payments.presentation.dto;

import java.math.BigDecimal;
import java.util.Map;

public record CheckoutRequest(
        String productName,
        BigDecimal price,
        Long userId,
        Map<String, String> metadata
) {}