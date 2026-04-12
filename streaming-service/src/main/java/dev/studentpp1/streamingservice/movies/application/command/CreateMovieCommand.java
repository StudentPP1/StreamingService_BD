package dev.studentpp1.streamingservice.movies.application.command;

import dev.studentpp1.streamingservice.movies.application.dto.MovieCreateRequest;

public record CreateMovieCommand(MovieCreateRequest request) {
}

