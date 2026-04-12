package dev.studentpp1.streamingservice.subscription.application.command;

import dev.studentpp1.streamingservice.subscription.application.dto.CreateFamilySubscriptionRequest;

public record CreateFamilySubscriptionCommand(CreateFamilySubscriptionRequest request, Long userId) {
}

