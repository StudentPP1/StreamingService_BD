package dev.studentpp1.streamingservice.movies.application.command.movie;

import dev.studentpp1.streamingservice.movies.domain.exception.MovieNotFoundException;
import dev.studentpp1.streamingservice.movies.domain.exception.OptimisticLockingException;
import dev.studentpp1.streamingservice.movies.domain.model.Movie;
import dev.studentpp1.streamingservice.movies.domain.repository.MovieRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

class UpdateMovieHandlerTest {

    private MovieRepository movieRepository;
    private UpdateMovieHandler handler;

    @BeforeEach
    void setUp() {
        movieRepository = mock(MovieRepository.class);
        handler = new UpdateMovieHandler(movieRepository);
    }

    @Test
    void handle_movieNotFound_throwsMovieNotFoundException() {
        when(movieRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> handler.handle(new UpdateMovieCommand(
                99L, "Inception", "desc", 2010, BigDecimal.valueOf(8.8), 1L, null
        )))
                .isInstanceOf(MovieNotFoundException.class);

        verify(movieRepository, never()).save(any());
    }

    @Test
    void handle_versionMismatch_throwsOptimisticLockingException() {
        Movie movie = Movie.restore(1L, "Inception", "desc", 2010,
                BigDecimal.valueOf(8.8), 1L, 5L);
        when(movieRepository.findById(1L)).thenReturn(Optional.of(movie));

        assertThatThrownBy(() -> handler.handle(new UpdateMovieCommand(
                1L, "Inception", "desc", 2010, BigDecimal.valueOf(8.8), 1L, 3L
        )))
                .isInstanceOf(OptimisticLockingException.class);

        verify(movieRepository, never()).save(any());
    }

    @Test
    void handle_validCommand_updatesAndSavesMovie() {
        Movie movie = Movie.restore(1L, "Inception", "desc", 2010,
                BigDecimal.valueOf(8.8), 1L, 0L);
        when(movieRepository.findById(1L)).thenReturn(Optional.of(movie));

        handler.handle(new UpdateMovieCommand(
                1L, "Interstellar", "new desc", 2014, BigDecimal.valueOf(9.0), 2L, 0L
        ));

        verify(movieRepository).save(movie);
    }
}
