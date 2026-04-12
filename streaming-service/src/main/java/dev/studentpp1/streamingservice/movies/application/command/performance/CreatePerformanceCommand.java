package dev.studentpp1.streamingservice.movies.application.command.performance;

public record CreatePerformanceCommand(
		String characterName,
		String description,
		Long actorId,
		Long movieId
) {
}
