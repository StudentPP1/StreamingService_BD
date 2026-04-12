package dev.studentpp1.streamingservice.subscription.application.command;

public record CancelSubscriptionCommand(Long subscriptionId, Long userId) {
}

