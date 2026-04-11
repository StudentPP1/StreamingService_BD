package dev.studentpp1.streamingservice.subscription.presentation.dto.response;

import java.math.BigDecimal;

public record SubscriptionPlanMovieDto(
        Long id,
        String title,
        String description,
        Integer year,
        BigDecimal rating
) {}