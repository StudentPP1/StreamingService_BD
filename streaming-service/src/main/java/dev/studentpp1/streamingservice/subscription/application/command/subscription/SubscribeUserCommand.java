package dev.studentpp1.streamingservice.subscription.application.command.subscription;

public record SubscribeUserCommand(Long planId, Long userId, String userEmail) {
}
