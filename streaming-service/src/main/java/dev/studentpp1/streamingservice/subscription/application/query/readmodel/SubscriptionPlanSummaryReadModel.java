package dev.studentpp1.streamingservice.subscription.application.query.readmodel;

import java.math.BigDecimal;

public record SubscriptionPlanSummaryReadModel(
        Long id,
        String name,
        String description,
        BigDecimal price,
        Integer duration,
        Long version
) {
}

