package dev.studentpp1.streamingservice.movies.application.query.movie;

import java.math.BigDecimal;

public record MovieReadModel(
        Long id,
        String title,
        String description,
        Integer year,
        BigDecimal rating,
        Long directorId,
        Long version
) {
}
