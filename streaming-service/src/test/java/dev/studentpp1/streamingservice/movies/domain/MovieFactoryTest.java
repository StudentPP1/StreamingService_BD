package dev.studentpp1.streamingservice.movies.domain;

import dev.studentpp1.streamingservice.movies.domain.exception.DirectorNotFoundException;
import dev.studentpp1.streamingservice.movies.domain.factory.MovieFactory;
import dev.studentpp1.streamingservice.movies.domain.model.Movie;
import dev.studentpp1.streamingservice.movies.domain.repository.DirectorRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

class MovieFactoryTest {

    private DirectorRepository directorRepository;
    private MovieFactory movieFactory;

    @BeforeEach
    void setUp() {
        directorRepository = mock(DirectorRepository.class);
        movieFactory = new MovieFactory(directorRepository);
    }

    @Test
    void create_validMovie_success() {
        when(directorRepository.existsById(1L)).thenReturn(true);

        Movie movie = movieFactory.create("Inception", "desc", 2010,
                BigDecimal.valueOf(8.8), 1L);

        assertThat(movie.getTitle()).isEqualTo("Inception");
        assertThat(movie.getId()).isNull();
        verify(directorRepository).existsById(1L);
    }

    @Test
    void create_directorNotExists_throwsDirectorNotFoundException() {
        when(directorRepository.existsById(99L)).thenReturn(false);

        assertThatThrownBy(() ->
                movieFactory.create("Inception", "desc", 2010,
                        BigDecimal.valueOf(8.8), 99L))
                .isInstanceOf(DirectorNotFoundException.class);
    }
}