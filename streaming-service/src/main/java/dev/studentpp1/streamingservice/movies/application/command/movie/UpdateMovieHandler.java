package dev.studentpp1.streamingservice.movies.application.command.movie;

import dev.studentpp1.streamingservice.movies.domain.exception.MovieNotFoundException;
import dev.studentpp1.streamingservice.movies.domain.model.Movie;
import dev.studentpp1.streamingservice.movies.domain.repository.MovieRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UpdateMovieHandler {
    private final MovieRepository movieRepository;

    public void handle(UpdateMovieCommand command) {
        Movie movie = movieRepository.findById(command.id())
                .orElseThrow(() -> new MovieNotFoundException(command.id()));
        movie.update(
                command.request().title(),
                command.request().description(),
                command.request().year(),
                command.request().rating(),
                command.request().directorId(),
                command.request().version()
        );
        movieRepository.save(movie);
    }
}
