package dev.studentpp1.streamingservice.subscription.application.query.readmodel;

import dev.studentpp1.streamingservice.subscription.domain.model.SubscriptionMovie;
import dev.studentpp1.streamingservice.subscription.domain.model.SubscriptionPlan;

import java.util.List;

public record SubscriptionPlanWithMoviesReadModel(SubscriptionPlan plan, List<SubscriptionMovie> movies) {
}

