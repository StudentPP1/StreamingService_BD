package dev.studentpp1.streamingservice.movies.presentation.dto.response;

import java.math.BigDecimal;

public record MovieDto(
        Long id,
        String title,
        String description,
        Integer year,
        BigDecimal rating,
        Long directorId,
        Long version
) {}