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
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class MovieControllerIntegrationTest extends AbstractPostgresContainerTest {

    private static final String CREATE_MOVIE_JSON = """
            {"title":"Inception","description":"desc","year":2010,"rating":8.8,"directorId":1}
            """;

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
        DirectorEntity director = saveDirector();
        movieJpaRepository.save(newMovie(director));

        mockMvc.perform(get("/api/movies")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].title").value("Inception"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createMovie_admin_returnsCreated() throws Exception {
        saveDirector();

        mockMvc.perform(post("/api/movies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(CREATE_MOVIE_JSON))
                .andExpect(status().isCreated());
    }

    @Test
    @WithMockUser
    void createMovie_userRole_returnsForbidden() throws Exception {
        mockMvc.perform(post("/api/movies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(CREATE_MOVIE_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser
    void getMovieById_notFound_returns404() throws Exception {
        mockMvc.perform(get("/api/movies/99999"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteMovie_notFound_returns404() throws Exception {

        mockMvc.perform(delete("/api/movies/99999"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser
    void getMovieById_exists_returnsOk() throws Exception {
        DirectorEntity director = saveDirector();
        MovieEntity movie = movieJpaRepository.save(newMovie(director));

        mockMvc.perform(get("/api/movies/" + movie.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Inception"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteMovie_exists_returnsNoContent() throws Exception {
        DirectorEntity director = saveDirector();
        MovieEntity movie = movieJpaRepository.save(newMovie(director));

        mockMvc.perform(delete("/api/movies/" + movie.getId()))
                .andExpect(status().isNoContent());
    }

    private DirectorEntity saveDirector() {
        return directorJpaRepository.save(new DirectorEntity(null, "Chris", "Nolan", "bio", null));
    }

    private MovieEntity newMovie(DirectorEntity director) {
        return new MovieEntity(null, "Inception", "desc", 2010, BigDecimal.valueOf(8.8), director, null, 0L);
    }
}