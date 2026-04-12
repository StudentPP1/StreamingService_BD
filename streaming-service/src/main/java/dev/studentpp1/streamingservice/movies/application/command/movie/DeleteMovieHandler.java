package dev.studentpp1.streamingservice.movies.application.command.movie;

import dev.studentpp1.streamingservice.movies.domain.exception.MovieNotFoundException;
import dev.studentpp1.streamingservice.movies.domain.repository.MovieRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DeleteMovieHandler {
    private final MovieRepository movieRepository;

    public void handle(DeleteMovieCommand command) {
        movieRepository.findById(command.id())
                .orElseThrow(() -> new MovieNotFoundException(command.id()));
        movieRepository.deleteById(command.id());
    }
}
