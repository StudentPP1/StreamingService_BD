package dev.studentpp1.streamingservice.movies.application.command;

import dev.studentpp1.streamingservice.movies.application.usecase.MovieService;
import dev.studentpp1.streamingservice.movies.domain.model.Movie;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MovieCommandHandler {

    private final MovieService movieService;

    public Movie handle(CreateMovieCommand command) {
        return movieService.createMovie(command.request());
    }

    public Movie handle(UpdateMovieCommand command) {
        return movieService.updateMovie(command.id(), command.request());
    }

    public void handle(DeleteMovieCommand command) {
        movieService.deleteMovie(command.id());
    }
}

