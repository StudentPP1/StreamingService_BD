package dev.studentpp1.streamingservice.subscription.application.command;

import dev.studentpp1.streamingservice.subscription.application.dto.SubscribeRequest;

public record SubscribeUserCommand(SubscribeRequest request, Long userId) {
}

