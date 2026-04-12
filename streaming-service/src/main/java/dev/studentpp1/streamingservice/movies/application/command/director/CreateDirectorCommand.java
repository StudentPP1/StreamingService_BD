package dev.studentpp1.streamingservice.movies.application.command.director;

public record CreateDirectorCommand(
		String name,
		String surname,
		String biography
) {
}
