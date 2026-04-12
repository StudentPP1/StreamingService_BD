package dev.studentpp1.streamingservice.subscription.application.command;

import dev.studentpp1.streamingservice.subscription.application.dto.CreateSubscriptionPlanRequest;

public record UpdatePlanCommand(Long id, CreateSubscriptionPlanRequest request) {
}

