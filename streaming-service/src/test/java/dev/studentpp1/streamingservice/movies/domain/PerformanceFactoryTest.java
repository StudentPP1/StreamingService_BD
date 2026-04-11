package dev.studentpp1.streamingservice.movies.domain;

import dev.studentpp1.streamingservice.movies.domain.exception.ActorNotFoundException;
import dev.studentpp1.streamingservice.movies.domain.exception.MovieNotFoundException;
import dev.studentpp1.streamingservice.movies.domain.factory.PerformanceFactory;
import dev.studentpp1.streamingservice.movies.domain.model.Performance;
import dev.studentpp1.streamingservice.movies.domain.repository.ActorRepository;
import dev.studentpp1.streamingservice.movies.domain.repository.MovieRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class PerformanceFactoryTest {

    private MovieRepository movieRepository;
    private ActorRepository actorRepository;
    private PerformanceFactory factory;

    @BeforeEach
    void setUp() {
        movieRepository = mock(MovieRepository.class);
        actorRepository = mock(ActorRepository.class);
        factory = new PerformanceFactory(movieRepository, actorRepository);
    }

    @Test
    void create_valid_success() {
        when(movieRepository.existsById(1L)).thenReturn(true);
        when(actorRepository.existsById(1L)).thenReturn(true);

        Performance p = factory.create(1L, 1L, "Cobb", "main role");

        assertThat(p.getCharacterName()).isEqualTo("Cobb");
        assertThat(p.getId()).isNull();
    }

    @Test
    void create_movieNotExists_throwsMovieNotFoundException() {
        when(movieRepository.existsById(99L)).thenReturn(false);

        assertThatThrownBy(() -> factory.create(99L, 1L, "Cobb", "desc"))
                .isInstanceOf(MovieNotFoundException.class);
    }

    @Test
    void create_actorNotExists_throwsActorNotFoundException() {
        when(movieRepository.existsById(1L)).thenReturn(true);
        when(actorRepository.existsById(99L)).thenReturn(false);

        assertThatThrownBy(() -> factory.create(1L, 99L, "Cobb", "desc"))
                .isInstanceOf(ActorNotFoundException.class);
    }
}