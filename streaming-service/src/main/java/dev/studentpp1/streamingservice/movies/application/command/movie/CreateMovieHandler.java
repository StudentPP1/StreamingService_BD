package dev.studentpp1.streamingservice.movies.application.command.movie;

import dev.studentpp1.streamingservice.movies.domain.factory.MovieFactory;
import dev.studentpp1.streamingservice.movies.domain.model.Movie;
import dev.studentpp1.streamingservice.movies.domain.repository.MovieRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CreateMovieHandler {
    private final MovieRepository movieRepository;
    private final MovieFactory movieFactory;

    public void handle(CreateMovieCommand command) {
        Movie movie = movieFactory.create(
                command.request().title(),
                command.request().description(),
                command.request().year(),
                command.request().rating(),
                command.request().directorId()
        );
        movieRepository.save(movie);
    }
}
