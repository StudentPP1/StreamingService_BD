package dev.studentpp1.streamingservice.subscription.application.query.readmodel;

import java.math.BigDecimal;

public record SubscriptionPlanMovieReadModel(
        Long id,
        String title,
        String description,
        Integer year,
        BigDecimal rating
) {
}

