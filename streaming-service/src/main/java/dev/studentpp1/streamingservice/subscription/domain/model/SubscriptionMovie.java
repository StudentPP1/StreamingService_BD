package dev.studentpp1.streamingservice.subscription.domain.model;

import java.math.BigDecimal;

public record SubscriptionMovie(
        Long id,
        String title,
        String description,
        Integer year,
        BigDecimal rating
) {}