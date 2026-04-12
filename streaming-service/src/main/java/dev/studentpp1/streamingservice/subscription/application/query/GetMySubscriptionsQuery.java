package dev.studentpp1.streamingservice.subscription.application.query;

public record GetMySubscriptionsQuery(Long userId, int page, int size) {
}

