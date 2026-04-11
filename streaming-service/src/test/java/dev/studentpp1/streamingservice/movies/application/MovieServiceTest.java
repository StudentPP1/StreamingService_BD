package dev.studentpp1.streamingservice.movies.application;

import dev.studentpp1.streamingservice.movies.application.usecase.MovieService;
import dev.studentpp1.streamingservice.movies.domain.exception.MovieNotFoundException;
import dev.studentpp1.streamingservice.movies.domain.factory.MovieFactory;
import dev.studentpp1.streamingservice.movies.domain.model.Movie;
import dev.studentpp1.streamingservice.movies.domain.repository.ActorRepository;
import dev.studentpp1.streamingservice.movies.domain.repository.DirectorRepository;
import dev.studentpp1.streamingservice.movies.domain.repository.MovieRepository;
import dev.studentpp1.streamingservice.movies.domain.repository.PerformanceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

class MovieServiceTest {

    private MovieRepository movieRepository;
    private MovieService movieService;

    @BeforeEach
    void setUp() {
        movieRepository = mock(MovieRepository.class);
        MovieFactory movieFactory = mock(MovieFactory.class);
        DirectorRepository directorRepository = mock(DirectorRepository.class);
        ActorRepository actorRepository = mock(ActorRepository.class);
        PerformanceRepository performanceRepository = mock(PerformanceRepository.class);

        movieService = new MovieService(
                movieRepository,
                directorRepository,
                performanceRepository,
                actorRepository,
                movieFactory
        );
    }

    @Test
    void getMovieById_exists_returnsMovie() {
        Movie movie = Movie.restore(1L, "Inception", "desc", 2010,
                BigDecimal.valueOf(8.8), 1L, 0L);
        when(movieRepository.findById(1L)).thenReturn(Optional.of(movie));

        Movie result = movieService.getMovieById(1L);

        assertThat(result.getTitle()).isEqualTo("Inception");
        verify(movieRepository).findById(1L);
    }

    @Test
    void getMovieById_notFound_throwsMovieNotFoundException() {
        when(movieRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> movieService.getMovieById(99L))
                .isInstanceOf(MovieNotFoundException.class);
    }

    @Test
    void deleteMovie_notFound_throwsMovieNotFoundException() {
        when(movieRepository.existsById(99L)).thenReturn(false);

        assertThatThrownBy(() -> movieService.deleteMovie(99L))
                .isInstanceOf(MovieNotFoundException.class);
        verify(movieRepository, never()).deleteById(any());
    }

    @Test
    void deleteMovie_exists_deletesMovie() {
        when(movieRepository.existsById(1L)).thenReturn(true);

        movieService.deleteMovie(1L);

        verify(movieRepository).deleteById(1L);
    }
}