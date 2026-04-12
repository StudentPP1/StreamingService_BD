package dev.studentpp1.streamingservice.subscription.application.query.readmodel;

import java.math.BigDecimal;
import java.util.List;

public record SubscriptionPlanDetailsReadModel(
        Long id,
        String name,
        String description,
        BigDecimal price,
        Integer duration,
        List<SubscriptionPlanMovieReadModel> includedMovies,
        Long version
) {
}

