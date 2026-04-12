package dev.studentpp1.streamingservice.movies.application.query.actor;

public record ActorReadModel(
        Long id,
        String name,
        String surname,
        String biography
) {
}
