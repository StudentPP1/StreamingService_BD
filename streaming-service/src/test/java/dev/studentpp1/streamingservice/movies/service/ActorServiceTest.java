package dev.studentpp1.streamingservice.movies.service;

import dev.studentpp1.streamingservice.movies.dto.ActorDetailDto;
import dev.studentpp1.streamingservice.movies.dto.ActorDto;
import dev.studentpp1.streamingservice.movies.dto.ActorRequest;
import dev.studentpp1.streamingservice.movies.entity.Actor;
import dev.studentpp1.streamingservice.movies.exception.ResourceNotFoundException;
import dev.studentpp1.streamingservice.movies.mapper.ActorMapper;
import dev.studentpp1.streamingservice.movies.repository.ActorRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ActorServiceTest {

    @Mock
    private ActorRepository actorRepository;
    @Mock
    private ActorMapper actorMapper;

    @InjectMocks
    private ActorService actorService;

    @Test
    void getActorById_ShouldReturnDto_WhenFound() {
        Long id = 1L;
        Actor actor = new Actor();
        actor.setId(id);
        ActorDto expectedDto = new ActorDto(id, "Leonardo", "DiCaprio", "Bio");

        when(actorRepository.findById(id)).thenReturn(Optional.of(actor));
        when(actorMapper.toDto(actor)).thenReturn(expectedDto);

        ActorDto result = actorService.getActorById(id);

        assertNotNull(result);
        assertEquals(expectedDto.name(), result.name());
        verify(actorRepository).findById(id);
    }

    @Test
    void getActorById_ShouldThrowException_WhenNotFound() {
        Long id = 999L;
        when(actorRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> actorService.getActorById(id));
    }

    @Test
    void getActorDetails_ShouldReturnDetailDto() {
        Long id = 1L;
        Actor actor = new Actor();
        actor.setId(id);
        ActorDetailDto expectedDto = new ActorDetailDto(id, "Leo", "Dicaprio", "Bio", null);

        when(actorRepository.findById(id)).thenReturn(Optional.of(actor));
        when(actorMapper.toDetailDto(actor)).thenReturn(expectedDto);

        ActorDetailDto result = actorService.getActorDetails(id);

        assertNotNull(result);
        assertEquals(expectedDto.name(), result.name());
    }

    @Test
    void createActor_ShouldSaveAndReturnDto() {
        ActorRequest request = new ActorRequest("Leonardo", "DiCaprio", "Bio");
        Actor entity = new Actor();
        Actor savedEntity = new Actor();
        savedEntity.setId(1L);
        ActorDto expectedDto = new ActorDto(1L, "Leonardo", "DiCaprio", "Bio");

        when(actorMapper.toEntity(request)).thenReturn(entity);
        when(actorRepository.save(entity)).thenReturn(savedEntity);
        when(actorMapper.toDto(savedEntity)).thenReturn(expectedDto);

        ActorDto result = actorService.createActor(request);

        assertNotNull(result);
        assertEquals(1L, result.id());
        verify(actorRepository).save(entity);
    }

    @Test
    void deleteActor_ShouldDelete_WhenExists() {
        Long id = 1L;
        when(actorRepository.existsById(id)).thenReturn(true);

        actorService.deleteActor(id);

        verify(actorRepository).deleteById(id);
    }

    @Test
    void deleteActor_ShouldThrowException_WhenNotExists() {
        Long id = 999L;
        when(actorRepository.existsById(id)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> actorService.deleteActor(id));
        verify(actorRepository, never()).deleteById(any());
    }
}