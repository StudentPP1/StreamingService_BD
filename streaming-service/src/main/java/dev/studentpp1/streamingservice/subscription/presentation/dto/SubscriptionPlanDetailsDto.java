package dev.studentpp1.streamingservice.subscription.presentation.dto;

import java.math.BigDecimal;
import java.util.Set;

public record SubscriptionPlanDetailsDto(
        Long id,
        String name,
        String description,
        BigDecimal price,
        Integer duration,
        Set<SubscriptionPlanMovieDto> movies
) {

}
