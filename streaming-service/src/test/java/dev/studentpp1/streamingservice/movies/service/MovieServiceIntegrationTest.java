package dev.studentpp1.streamingservice.movies.service;

import dev.studentpp1.streamingservice.AbstractPostgresContainerTest;
import dev.studentpp1.streamingservice.movies.dto.MovieDto;
import dev.studentpp1.streamingservice.movies.dto.MovieRequest;
import dev.studentpp1.streamingservice.movies.entity.Director;
import dev.studentpp1.streamingservice.movies.exception.OptimisticLockingException;
import dev.studentpp1.streamingservice.movies.repository.DirectorRepository;
import dev.studentpp1.streamingservice.movies.repository.MovieRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@TestPropertySource(properties = "spring.jpa.hibernate.ddl-auto=create-drop")
public class MovieServiceIntegrationTest extends AbstractPostgresContainerTest {

    @Autowired
    private MovieService movieService;

    @Autowired
    private MovieRepository movieRepository;

    @Autowired
    private DirectorRepository directorRepository;

    private Long directorId;

    @BeforeEach
    void setUp() {
        movieRepository.deleteAll();
        directorRepository.deleteAll();

        Director director = new Director();
        director.setName("Denis");
        director.setSurname("Villeneuve");
        director = directorRepository.save(director);
        directorId = director.getId();
    }

    @Test
    void testOptimisticLocking_ShouldThrowException_WhenVersionsMismatch() {
        MovieRequest createRequest = new MovieRequest(
                "Dune",
                "Epic sci-fi",
                2021,
                new BigDecimal("8.0"),
                directorId,
                null
        );
        MovieDto createdMovie = movieService.createMovie(createRequest);
        Long movieId = createdMovie.id();

        MovieDto admin1View = movieService.getMovieById(movieId);
        MovieDto admin2View = movieService.getMovieById(movieId);

        Assertions.assertEquals(admin1View.version(), admin2View.version(), "Версії мають співпадати на початку");

        MovieRequest updateRequest1 = new MovieRequest(
                "Dune: Part One",
                "Epic sci-fi",
                2021,
                new BigDecimal("8.5"),
                directorId,
                admin1View.version()
        );
        movieService.updateMovie(movieId, updateRequest1);


        MovieRequest updateRequest2 = new MovieRequest(
                "Dune (Old Title)",
                "Description changed by Admin 2",
                2021,
                new BigDecimal("8.0"),
                directorId,
                admin2View.version()
        );

        assertThrows(OptimisticLockingException.class, () -> {
            movieService.updateMovie(movieId, updateRequest2);
        });
    }
}