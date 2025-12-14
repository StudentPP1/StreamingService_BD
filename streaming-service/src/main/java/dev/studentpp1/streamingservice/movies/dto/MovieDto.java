package dev.studentpp1.streamingservice.movies.dto;

import java.math.BigDecimal;

public record MovieDto(
        Long id,
        String title,
        String description,
        Integer year,
        BigDecimal rating,
        Long directorId
) {}