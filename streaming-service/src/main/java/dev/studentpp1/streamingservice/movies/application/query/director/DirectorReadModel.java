package dev.studentpp1.streamingservice.movies.application.query.director;

public record DirectorReadModel(
        Long id,
        String name,
        String surname,
        String biography
) {
}
