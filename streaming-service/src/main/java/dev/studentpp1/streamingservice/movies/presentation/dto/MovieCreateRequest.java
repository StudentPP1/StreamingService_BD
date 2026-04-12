package dev.studentpp1.streamingservice.movies.presentation.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record MovieCreateRequest(
        @NotBlank(message = "Title is required")
        @Size(max = 255, message = "Title must be less than 255 characters")
        String title,

        String description,

        @NotNull(message = "Year is required")
        @Min(value = 1881, message = "Year must be greater than 1880")
        Integer year,

        @DecimalMin(value = "0.0", message = "Rating must be at least 0.0")
        @DecimalMax(value = "10.0", message = "Rating must be at most 10.0")
        BigDecimal rating,

        @NotNull(message = "Director ID is required")
        Long directorId,

        Long version
) {
}

