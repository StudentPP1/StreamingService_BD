package dev.studentpp1.streamingservice.movies.application.command.actor;

public record UpdateActorCommand(
		Long id,
		String name,
		String surname,
		String biography
) {
}
