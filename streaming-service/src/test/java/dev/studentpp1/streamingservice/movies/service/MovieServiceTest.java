package dev.studentpp1.streamingservice.movies.service;

import dev.studentpp1.streamingservice.movies.dto.MovieDto;
import dev.studentpp1.streamingservice.movies.dto.MovieRequest;
import dev.studentpp1.streamingservice.movies.entity.Director;
import dev.studentpp1.streamingservice.movies.entity.Movie;
import dev.studentpp1.streamingservice.movies.exception.ResourceNotFoundException;
import dev.studentpp1.streamingservice.movies.mapper.MovieMapper;
import dev.studentpp1.streamingservice.movies.repository.DirectorRepository;
import dev.studentpp1.streamingservice.movies.repository.MovieRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MovieServiceTest {

    @Mock
    private MovieRepository movieRepository;
    @Mock
    private DirectorRepository directorRepository;
    @Mock
    private MovieMapper movieMapper;

    @InjectMocks
    private MovieService movieService;

    @Test
    void getMovieById_ShouldReturnDto_WhenFound() {
        Long id = 1L;
        Movie movie = new Movie();
        movie.setId(id);
        MovieDto expectedDto = new MovieDto(id, "Inception", "Desc", 2010, BigDecimal.TEN, 1L);

        when(movieRepository.findById(id)).thenReturn(Optional.of(movie));
        when(movieMapper.toDto(movie)).thenReturn(expectedDto);

        MovieDto result = movieService.getMovieById(id);

        assertNotNull(result);
        assertEquals("Inception", result.title());
    }

    @Test
    void createMovie_ShouldSave_WhenDirectorExistsAndYearValid() {
        Long directorId = 1L;
        MovieRequest request = new MovieRequest("Inception", "Desc", 2010, BigDecimal.TEN, directorId);
        Director director = new Director();
        director.setId(directorId);

        Movie movieEntity = new Movie();
        movieEntity.setYear(2010);

        Movie savedMovie = new Movie();
        savedMovie.setId(10L);

        MovieDto expectedDto = new MovieDto(10L, "Inception", "Desc", 2010, BigDecimal.TEN, directorId);

        // Мокаємо поведінку
        when(directorRepository.findById(directorId)).thenReturn(Optional.of(director));
        when(movieMapper.toEntity(request)).thenReturn(movieEntity);
        when(movieRepository.save(movieEntity)).thenReturn(savedMovie);
        when(movieMapper.toDto(savedMovie)).thenReturn(expectedDto);

        MovieDto result = movieService.createMovie(request);

        assertNotNull(result);
        assertEquals(10L, result.id());

        assertEquals(director, movieEntity.getDirector());

        verify(movieRepository).save(movieEntity);
    }

    @Test
    void createMovie_ShouldThrowException_WhenDirectorNotFound() {
        MovieRequest request = new MovieRequest("Title", "Desc", 2020, BigDecimal.TEN, 99L);

        when(directorRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> movieService.createMovie(request));
        verify(movieRepository, never()).save(any());
    }

    @Test
    void createMovie_ShouldThrowException_WhenYearIsOld() {
        Long directorId = 1L;
        MovieRequest request = new MovieRequest("Old Movie", "Desc", 1800, BigDecimal.TEN, directorId);
        Director director = new Director();

        Movie movieEntity = new Movie();
        movieEntity.setYear(1800);

        when(directorRepository.findById(directorId)).thenReturn(Optional.of(director));
        when(movieMapper.toEntity(request)).thenReturn(movieEntity);

        assertThrows(IllegalArgumentException.class, () -> movieService.createMovie(request));
        verify(movieRepository, never()).save(any());
    }
}