package dev.studentpp1.streamingservice.movies.application.query.performance;

public record PerformanceReadModel(
        Long id,
        String characterName,
        String description,
        Long actorId,
        Long movieId
) {
}
