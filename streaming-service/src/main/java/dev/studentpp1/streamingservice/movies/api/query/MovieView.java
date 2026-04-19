package dev.studentpp1.streamingservice.movies.api.query;

import java.math.BigDecimal;

public record MovieView(
        Long id,
        String title,
        String description,
        int year,
        BigDecimal rating
) {
}

