package dev.studentpp1.streamingservice.movies.application.command.movie;

import dev.studentpp1.streamingservice.movies.domain.factory.MovieFactory;
import dev.studentpp1.streamingservice.movies.domain.model.Movie;
import dev.studentpp1.streamingservice.movies.domain.repository.MovieRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.mockito.Mockito.*;

class CreateMovieHandlerTest {

    private MovieRepository movieRepository;
    private MovieFactory movieFactory;
    private CreateMovieHandler handler;

    @BeforeEach
    void setUp() {
        movieRepository = mock(MovieRepository.class);
        movieFactory = mock(MovieFactory.class);
        handler = new CreateMovieHandler(movieRepository, movieFactory);
    }

    @Test
    void handle_validCommand_createsAndSavesMovie() {
        MovieCreateRequest request = new MovieCreateRequest(
                "Inception", "desc", 2010, BigDecimal.valueOf(8.8), 1L, null);
        Movie movie = Movie.create("Inception", "desc", 2010, BigDecimal.valueOf(8.8), 1L);

        when(movieFactory.create("Inception", "desc", 2010, BigDecimal.valueOf(8.8), 1L))
                .thenReturn(movie);

        handler.handle(new CreateMovieCommand(request));

        verify(movieFactory).create("Inception", "desc", 2010, BigDecimal.valueOf(8.8), 1L);
        verify(movieRepository).save(movie);
    }
}
