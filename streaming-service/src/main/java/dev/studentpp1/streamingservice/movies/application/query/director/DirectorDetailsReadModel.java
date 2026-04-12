package dev.studentpp1.streamingservice.movies.application.query.director;

import dev.studentpp1.streamingservice.movies.application.query.movie.MovieReadModel;

import java.util.List;

public record DirectorDetailsReadModel(
        Long id,
        String name,
        String surname,
        String biography,
        List<MovieReadModel> movies
) {
}
