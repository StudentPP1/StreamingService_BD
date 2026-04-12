package dev.studentpp1.streamingservice.subscription.presentation.dto;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.util.List;

public record CreateSubscriptionPlanRequest(
        @NotBlank(message = "Name is required")
        @Size(max = 150, message = "Name must not exceed 150 characters")
        String name,

        @NotBlank(message = "Description is required")
        String description,

        @NotNull(message = "Price is required")
        @PositiveOrZero(message = "Price must be zero or positive")
        @Digits(integer = 6, fraction = 2, message = "Price format: max 6 digits before dot, 2 after")
        BigDecimal price,

        @NotNull(message = "Duration is required")
        @Positive(message = "Duration must be positive")
        Integer duration,

        List<Long> includedMovieIds
) {
}

