package dev.studentpp1.streamingservice.movies.application.query.movie;

public record MovieCastItemReadModel(
        Long actorId,
        String actorName,
        String actorSurname,
        String characterName
) {
}
