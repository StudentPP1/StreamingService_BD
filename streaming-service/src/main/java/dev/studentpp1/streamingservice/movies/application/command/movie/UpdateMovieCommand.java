package dev.studentpp1.streamingservice.movies.application.command.movie;

public record UpdateMovieCommand(Long id, MovieCreateRequest request) {
}
