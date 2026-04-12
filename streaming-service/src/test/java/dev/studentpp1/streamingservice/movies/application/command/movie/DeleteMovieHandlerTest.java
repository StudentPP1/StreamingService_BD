package dev.studentpp1.streamingservice.movies.application.command.movie;

import dev.studentpp1.streamingservice.movies.domain.exception.MovieNotFoundException;
import dev.studentpp1.streamingservice.movies.domain.model.Movie;
import dev.studentpp1.streamingservice.movies.domain.repository.MovieRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

class DeleteMovieHandlerTest {

    private MovieRepository movieRepository;
    private DeleteMovieHandler handler;

    @BeforeEach
    void setUp() {
        movieRepository = mock(MovieRepository.class);
        handler = new DeleteMovieHandler(movieRepository);
    }

    @Test
    void handle_movieNotFound_throwsMovieNotFoundException() {
        when(movieRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> handler.handle(new DeleteMovieCommand(99L)))
                .isInstanceOf(MovieNotFoundException.class);

        verify(movieRepository, never()).deleteById(any());
    }

    @Test
    void handle_movieExists_deletesMovie() {
        Movie movie = Movie.restore(1L, "Inception", "desc", 2010,
                BigDecimal.valueOf(8.8), 1L, 0L);
        when(movieRepository.findById(1L)).thenReturn(Optional.of(movie));

        handler.handle(new DeleteMovieCommand(1L));

        verify(movieRepository).deleteById(1L);
    }
}
