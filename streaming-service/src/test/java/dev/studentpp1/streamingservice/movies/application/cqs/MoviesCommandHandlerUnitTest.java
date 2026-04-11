package dev.studentpp1.streamingservice.movies.application.cqs;

import dev.studentpp1.streamingservice.movies.application.usecase.ActorService;
import dev.studentpp1.streamingservice.movies.application.usecase.DirectorService;
import dev.studentpp1.streamingservice.movies.application.usecase.MovieService;
import dev.studentpp1.streamingservice.movies.application.usecase.PerformanceService;
import dev.studentpp1.streamingservice.movies.domain.model.Movie;
import dev.studentpp1.streamingservice.movies.presentation.dto.request.MovieCreateRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MoviesCommandHandlerUnitTest {
    @Mock
    private MovieService movieService;
    @Mock
    private ActorService actorService;
    @Mock
    private DirectorService directorService;
    @Mock
    private PerformanceService performanceService;
    @Mock
    private Movie movie;
    private MoviesCommandHandler handler;

    @BeforeEach
    void setUp() {
        handler = new MoviesCommandHandler(movieService, actorService, directorService, performanceService);
    }

    @Test
    void createMovie_returnsCreatedId() {
        MovieCreateRequest request = new MovieCreateRequest(
                "Inception", "desc", 2010, BigDecimal.valueOf(8.8), 1L, null);
        when(movieService.createMovie(request)).thenReturn(movie);
        when(movie.getId()).thenReturn(15L);
        Long result = handler.handle(new MoviesCqs.CreateMovieCommand(request));
        assertThat(result).isEqualTo(15L);
        verify(movieService).createMovie(request);
    }

    @Test
    void deletePerformance_delegatesToService() {
        handler.handle(new MoviesCqs.DeletePerformanceCommand(99L));
        verify(performanceService).deletePerformance(99L);
    }
}


