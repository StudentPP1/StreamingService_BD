package dev.studentpp1.streamingservice.movies.integration;

import dev.studentpp1.streamingservice.AbstractPostgresContainerTest;
import dev.studentpp1.streamingservice.movies.infrastructure.entity.DirectorEntity;
import dev.studentpp1.streamingservice.movies.infrastructure.entity.MovieEntity;
import dev.studentpp1.streamingservice.movies.infrastructure.repository.DirectorJpaRepository;
import dev.studentpp1.streamingservice.movies.infrastructure.repository.MovieJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class MovieControllerQueryIntegrationTest extends AbstractPostgresContainerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private MovieJpaRepository movieJpaRepository;

    @Autowired
    private DirectorJpaRepository directorJpaRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUp() {
        jdbcTemplate.execute("TRUNCATE TABLE performance, movie, director RESTART IDENTITY CASCADE");
    }

    @Test
    @WithMockUser
    void getAllMovies_authenticated_returnsOk() throws Exception {
        DirectorEntity director = directorJpaRepository.save(
                new DirectorEntity(null, "Chris", "Nolan", "bio", null)
        );
        movieJpaRepository.save(new MovieEntity(
                null,
                "Inception",
                "desc",
                2010,
                BigDecimal.valueOf(8.8),
                director,
                null,
                0L
        ));

        mockMvc.perform(get("/api/movies")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].title").value("Inception"));
    }

    @Test
    @WithMockUser
    void getMovieById_notFound_returns404() throws Exception {
        mockMvc.perform(get("/api/movies/99999"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser
    void getMovieById_returnsMovieDto() throws Exception {
        DirectorEntity director = directorJpaRepository.save(
                new DirectorEntity(null, "Denis", "Villeneuve", "bio", null)
        );
        MovieEntity movie = movieJpaRepository.save(new MovieEntity(
                null,
                "Dune",
                "epic",
                2021,
                BigDecimal.valueOf(8.2),
                director,
                null,
                0L
        ));

        mockMvc.perform(get("/api/movies/{id}", movie.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(movie.getId()))
                .andExpect(jsonPath("$.title").value("Dune"));
    }

    @Test
    @WithMockUser
    void getMovieDetails_returnsMovieDetailDto() throws Exception {
        DirectorEntity director = directorJpaRepository.save(
                new DirectorEntity(null, "Christopher", "Nolan", "bio", null)
        );
        MovieEntity movie = movieJpaRepository.save(new MovieEntity(
                null,
                "Interstellar",
                "space",
                2014,
                BigDecimal.valueOf(8.6),
                director,
                null,
                0L
        ));

        mockMvc.perform(get("/api/movies/{id}/details", movie.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(movie.getId()))
                .andExpect(jsonPath("$.director.id").value(director.getId()))
                .andExpect(jsonPath("$.title").value("Interstellar"));
    }
}
