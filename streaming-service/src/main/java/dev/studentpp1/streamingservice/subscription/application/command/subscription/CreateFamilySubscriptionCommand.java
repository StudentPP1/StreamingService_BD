package dev.studentpp1.streamingservice.subscription.application.command.subscription;

import java.util.List;

public record CreateFamilySubscriptionCommand(Long planId, List<String> memberEmails, Long userId) {
}

