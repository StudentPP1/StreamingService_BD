package dev.studentpp1.streamingservice.movies.application.query.actor;

public record ActorFilmographyItemReadModel(
        Long movieId,
        String movieTitle,
        Integer movieYear,
        String characterName
) {
}
