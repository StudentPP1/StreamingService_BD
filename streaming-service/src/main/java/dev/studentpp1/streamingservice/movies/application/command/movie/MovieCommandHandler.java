package dev.studentpp1.streamingservice.movies.application.command.movie;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MovieCommandHandler {
    private final CreateMovieHandler createMovieHandler;
    private final UpdateMovieHandler updateMovieHandler;
    private final DeleteMovieHandler deleteMovieHandler;

    public void handle(CreateMovieCommand command) {
        createMovieHandler.handle(command);
    }

    public void handle(UpdateMovieCommand command) {
        updateMovieHandler.handle(command);
    }

    public void handle(DeleteMovieCommand command) {
        deleteMovieHandler.handle(command);
    }
}
