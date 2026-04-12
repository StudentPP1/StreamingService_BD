package dev.studentpp1.streamingservice.subscription.application.command.subscription;

public record CancelSubscriptionCommand(Long subscriptionId, Long userId) {
}

