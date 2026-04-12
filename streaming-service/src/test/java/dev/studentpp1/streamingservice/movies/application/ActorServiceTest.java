package dev.studentpp1.streamingservice.movies.application;

import dev.studentpp1.streamingservice.movies.application.usecase.ActorService;
import dev.studentpp1.streamingservice.movies.domain.exception.ActorNotFoundException;
import dev.studentpp1.streamingservice.movies.domain.factory.ActorFactory;
import dev.studentpp1.streamingservice.movies.domain.model.Actor;
import dev.studentpp1.streamingservice.movies.domain.repository.ActorRepository;
import dev.studentpp1.streamingservice.movies.domain.repository.MovieRepository;
import dev.studentpp1.streamingservice.movies.domain.repository.PerformanceRepository;
import dev.studentpp1.streamingservice.movies.application.dto.ActorCreateRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class ActorServiceTest {

    private ActorRepository actorRepository;
    private ActorService actorService;

    @BeforeEach
    void setUp() {
        actorRepository = mock(ActorRepository.class);
        MovieRepository movieRepository = mock(MovieRepository.class);
        PerformanceRepository performanceRepository = mock(PerformanceRepository.class);
        ActorFactory actorFactory = mock(ActorFactory.class);

        actorService = new ActorService(
                actorRepository, movieRepository,
                performanceRepository, actorFactory
        );
    }

    @Test
    void getActorById_exists_returnsActor() {
        Actor actor = Actor.restore(1L, "Leonardo", "DiCaprio", "bio");
        when(actorRepository.findById(1L)).thenReturn(Optional.of(actor));

        Actor result = actorService.getActorById(1L);

        assertThat(result.getSurname()).isEqualTo("DiCaprio");
    }

    @Test
    void getActorById_notFound_throwsActorNotFoundException() {
        when(actorRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> actorService.getActorById(99L))
                .isInstanceOf(ActorNotFoundException.class);
    }

    @Test
    void updateActor_callsUpdateOnDomain() {
        Actor actor = Actor.restore(1L, "Leonardo", "DiCaprio", "bio");
        when(actorRepository.findById(1L)).thenReturn(Optional.of(actor));
        when(actorRepository.save(actor)).thenReturn(actor);

        var request = new ActorCreateRequest("Brad", "Pitt", "new bio");

        actorService.updateActor(1L, request);

        assertThat(actor.getName()).isEqualTo("Brad");
        verify(actorRepository).save(actor);
    }
}