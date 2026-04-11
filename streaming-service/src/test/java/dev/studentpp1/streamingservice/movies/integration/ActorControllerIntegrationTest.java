package dev.studentpp1.streamingservice.movies.integration;

import dev.studentpp1.streamingservice.AbstractPostgresContainerTest;
import dev.studentpp1.streamingservice.movies.infrastructure.entity.ActorEntity;
import dev.studentpp1.streamingservice.movies.infrastructure.repository.ActorJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class ActorControllerIntegrationTest extends AbstractPostgresContainerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ActorJpaRepository actorJpaRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUp() {
        jdbcTemplate.execute("TRUNCATE TABLE performance, actor RESTART IDENTITY CASCADE");
    }

    @Test
    @WithMockUser
    void getAllActors_authenticated_returnsOk() throws Exception {
        actorJpaRepository.save(new ActorEntity(null, "Leonardo", "DiCaprio", "bio", null));

        mockMvc.perform(get("/api/actors")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].name").value("Leonardo"));
    }

    @Test
    void getAllActors_unauthenticated_returnsForbidden() throws Exception {
        mockMvc.perform(get("/api/actors"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createActor_admin_returnsCreated() throws Exception {
        String json = """
                {"name":"Leonardo","surname":"DiCaprio","biography":"bio"}
                """;

        mockMvc.perform(post("/api/actors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Leonardo"))
                .andExpect(jsonPath("$.id").value(1));
    }


    @Test
    @WithMockUser
    void getActorById_notFound_returns404() throws Exception {

        mockMvc.perform(get("/api/actors/99999"))
                .andExpect(status().isNotFound());
    }
}