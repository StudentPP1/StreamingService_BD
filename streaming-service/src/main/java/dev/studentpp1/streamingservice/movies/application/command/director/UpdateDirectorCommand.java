package dev.studentpp1.streamingservice.movies.application.command.director;

public record UpdateDirectorCommand(
		Long id,
		String name,
		String surname,
		String biography
) {
}
