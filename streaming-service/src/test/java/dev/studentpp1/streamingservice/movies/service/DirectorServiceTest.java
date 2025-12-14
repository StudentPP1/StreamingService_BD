package dev.studentpp1.streamingservice.movies.service;

import dev.studentpp1.streamingservice.movies.exception.ResourceNotFoundException;
import dev.studentpp1.streamingservice.movies.dto.DirectorDto;
import dev.studentpp1.streamingservice.movies.dto.DirectorRequest;
import dev.studentpp1.streamingservice.movies.entity.Director;
import dev.studentpp1.streamingservice.movies.mapper.DirectorMapper;
import dev.studentpp1.streamingservice.movies.mapper.MovieMapper;
import dev.studentpp1.streamingservice.movies.repository.DirectorRepository;
import dev.studentpp1.streamingservice.movies.repository.MovieRepository;
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
class DirectorServiceTest {

    @Mock
    private DirectorRepository directorRepository;
    @Mock
    private MovieRepository movieRepository;
    @Mock
    private DirectorMapper directorMapper;
    @Mock
    private MovieMapper movieMapper;

    @InjectMocks
    private DirectorService directorService;

    @Test
    void getDirectorById_ShouldReturnDto_WhenFound() {
        Long id = 1L;
        Director director = new Director();
        director.setId(id);
        DirectorDto expectedDto = new DirectorDto(id, "Tarantino", "Quentin", "Bio");

        when(directorRepository.findById(id)).thenReturn(Optional.of(director));
        when(directorMapper.toDto(director)).thenReturn(expectedDto);

        DirectorDto result = directorService.getDirectorById(id);

        assertNotNull(result);
        assertEquals(expectedDto.name(), result.name());
        verify(directorRepository).findById(id);
    }

    @Test
    void getDirectorById_ShouldThrowException_WhenNotFound() {
        // Arrange
        Long id = 999L;
        when(directorRepository.findById(id)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> directorService.getDirectorById(id));
    }

    @Test
    void createDirector_ShouldSaveAndReturnDto() {
        DirectorRequest request = new DirectorRequest("Nolan", "Christopher", "Bio");
        Director entity = new Director();
        Director savedEntity = new Director();
        savedEntity.setId(1L);
        DirectorDto expectedDto = new DirectorDto(1L, "Nolan", "Christopher", "Bio");

        when(directorMapper.toEntity(request)).thenReturn(entity);
        when(directorRepository.save(entity)).thenReturn(savedEntity);
        when(directorMapper.toDto(savedEntity)).thenReturn(expectedDto);

        DirectorDto result = directorService.createDirector(request);

        assertEquals(1L, result.id());
        verify(directorRepository).save(entity);
    }
}