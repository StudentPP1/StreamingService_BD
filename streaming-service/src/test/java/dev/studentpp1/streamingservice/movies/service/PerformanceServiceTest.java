package dev.studentpp1.streamingservice.movies.service;

import dev.studentpp1.streamingservice.movies.dto.PerformanceDto;
import dev.studentpp1.streamingservice.movies.dto.PerformanceRequest;
import dev.studentpp1.streamingservice.movies.entity.Actor;
import dev.studentpp1.streamingservice.movies.entity.Movie;
import dev.studentpp1.streamingservice.movies.entity.Performance;
import dev.studentpp1.streamingservice.movies.exception.ResourceNotFoundException;
import dev.studentpp1.streamingservice.movies.mapper.PerformanceMapper;
import dev.studentpp1.streamingservice.movies.repository.ActorRepository;
import dev.studentpp1.streamingservice.movies.repository.MovieRepository;
import dev.studentpp1.streamingservice.movies.repository.PerformanceRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PerformanceServiceTest {

    @Mock
    private PerformanceRepository performanceRepository;
    @Mock
    private ActorRepository actorRepository;
    @Mock
    private MovieRepository movieRepository;
    @Mock
    private PerformanceMapper performanceMapper;

    @InjectMocks
    private PerformanceService performanceService;

    @Test
    void getPerformanceById_ShouldReturnDto_WhenFound() {
        Long id = 1L;
        Performance performance = new Performance();
        performance.setId(id);
        PerformanceDto expectedDto = new PerformanceDto(id, "Joker", "Desc", 1L, 1L);

        when(performanceRepository.findById(id)).thenReturn(Optional.of(performance));
        when(performanceMapper.toDto(performance)).thenReturn(expectedDto);

        PerformanceDto result = performanceService.getPerformanceById(id);

        assertNotNull(result);
        assertEquals("Joker", result.characterName());
    }

    @Test
    void createPerformance_ShouldSave_WhenActorAndMovieExist() {
        Long actorId = 10L;
        Long movieId = 20L;
        PerformanceRequest request = new PerformanceRequest("Neo", "Main char", actorId, movieId);

        Actor actor = new Actor();
        actor.setId(actorId);
        Movie movie = new Movie();
        movie.setId(movieId);

        Performance entity = new Performance();
        Performance savedEntity = new Performance();
        savedEntity.setId(5L);
        PerformanceDto expectedDto = new PerformanceDto(5L, "Neo", "Main char", actorId, movieId);

        when(actorRepository.findById(actorId)).thenReturn(Optional.of(actor));
        when(movieRepository.findById(movieId)).thenReturn(Optional.of(movie));

        when(performanceMapper.toEntity(request)).thenReturn(entity);
        when(performanceRepository.save(entity)).thenReturn(savedEntity);
        when(performanceMapper.toDto(savedEntity)).thenReturn(expectedDto);

        PerformanceDto result = performanceService.createPerformance(request);

        assertNotNull(result);
        assertEquals(5L, result.id());

        assertEquals(actor, entity.getActor());
        assertEquals(movie, entity.getMovie());

        verify(performanceRepository).save(entity);
    }

    @Test
    void createPerformance_ShouldThrow_WhenActorNotFound() {
        PerformanceRequest request = new PerformanceRequest("Neo", "Desc", 99L, 1L);
        when(actorRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> performanceService.createPerformance(request));
        verify(movieRepository, never()).findById(any());
        verify(performanceRepository, never()).save(any());
    }

    @Test
    void deletePerformance_ShouldDelete_WhenExists() {
        Long id = 1L;
        when(performanceRepository.existsById(id)).thenReturn(true);

        performanceService.deletePerformance(id);

        verify(performanceRepository).deleteById(id);
    }
}