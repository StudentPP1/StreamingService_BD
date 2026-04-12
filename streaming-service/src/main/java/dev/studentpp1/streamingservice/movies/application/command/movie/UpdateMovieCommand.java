package dev.studentpp1.streamingservice.movies.application.command.movie;

import java.math.BigDecimal;

public record UpdateMovieCommand(
		Long id,
		String title,
		String description,
		Integer year,
		BigDecimal rating,
		Long directorId,
		Long version
) {
}
