package dev.studentpp1.streamingservice.subscription.application.query.readmodel;

import dev.studentpp1.streamingservice.subscription.domain.model.UserSubscription;

public record UserSubscriptionWithPlanReadModel(UserSubscription subscription, String planName) {
}

