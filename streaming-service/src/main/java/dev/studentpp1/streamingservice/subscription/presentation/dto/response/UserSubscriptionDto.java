package dev.studentpp1.streamingservice.subscription.presentation.dto.response;

import dev.studentpp1.streamingservice.subscription.domain.model.SubscriptionStatus;

import java.time.LocalDateTime;

public record UserSubscriptionDto(
    Long id,
    LocalDateTime startTime,
    LocalDateTime endTime,
    SubscriptionStatus status,
    String planName
) {

}
