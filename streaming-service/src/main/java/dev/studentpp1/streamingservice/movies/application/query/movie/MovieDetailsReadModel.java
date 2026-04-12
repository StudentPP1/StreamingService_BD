package dev.studentpp1.streamingservice.movies.application.query.movie;

import dev.studentpp1.streamingservice.movies.application.query.director.DirectorReadModel;

import java.math.BigDecimal;
import java.util.List;

public record MovieDetailsReadModel(
        Long id,
        String title,
        String description,
        Integer year,
        BigDecimal rating,
        Long version,
        DirectorReadModel director,
        List<MovieCastItemReadModel> cast
) {
}
