package dev.studentpp1.streamingservice.subscription.integration;

import dev.studentpp1.streamingservice.AbstractPostgresContainerTest;
import dev.studentpp1.streamingservice.movies.infrastructure.entity.DirectorEntity;
import dev.studentpp1.streamingservice.movies.infrastructure.entity.MovieEntity;
import dev.studentpp1.streamingservice.movies.infrastructure.repository.DirectorJpaRepository;
import dev.studentpp1.streamingservice.movies.infrastructure.repository.MovieJpaRepository;
import dev.studentpp1.streamingservice.subscription.infrastructure.entity.SubscriptionPlanEntity;
import dev.studentpp1.streamingservice.subscription.infrastructure.repository.SubscriptionPlanJpaRepository;
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
import java.util.Set;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class SubscriptionPlanControllerIntegrationTest extends AbstractPostgresContainerTest {

    private static final String CREATE_PLAN_JSON = """
            {"name":"Basic","description":"Basic plan","price":9.99,"duration":30}
            """;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private SubscriptionPlanJpaRepository subscriptionPlanJpaRepository;

    @Autowired
    private DirectorJpaRepository directorJpaRepository;

    @Autowired
    private MovieJpaRepository movieJpaRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUp() {
        jdbcTemplate.execute("TRUNCATE TABLE included_movie, user_subscription, subscription_plan RESTART IDENTITY CASCADE");
    }

    @Test
    void getAllPlans_noAuth_returnsOk() throws Exception {
        savePlan("Basic", BigDecimal.valueOf(9.99));

        mockMvc.perform(get("/api/subscription-plans")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].name").value("Basic"));
    }

    @Test
    void getPlanById_notFound_returns404() throws Exception {
        mockMvc.perform(get("/api/subscription-plans/99999"))
                .andExpect(status().isNotFound());
    }


    @Test
    @WithMockUser
    void createPlan_userRole_returnsForbidden() throws Exception {
        mockMvc.perform(post("/api/subscription-plans")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(CREATE_PLAN_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createPlan_admin_returnsCreated() throws Exception {
        mockMvc.perform(post("/api/subscription-plans")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(CREATE_PLAN_JSON))
                .andExpect(status().isCreated())
                .andExpect(content().string(""));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deletePlan_notFound_returns404() throws Exception {

        mockMvc.perform(delete("/api/subscription-plans/99999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getAllPlans_withSearch_filtersByName() throws Exception {
        savePlan("Basic", BigDecimal.valueOf(9.99));
        savePlan("Premium", BigDecimal.valueOf(19.99));

        mockMvc.perform(get("/api/subscription-plans")
                        .param("search", "prem")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.content[0].name").value("Premium"));
    }

    @Test
    void getAllPlans_withPagination_returnsPageMetadata() throws Exception {
        savePlan("Basic", BigDecimal.valueOf(9.99));
        savePlan("Premium", BigDecimal.valueOf(19.99));

        mockMvc.perform(get("/api/subscription-plans")
                        .param("page", "1")
                        .param("size", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.page").value(1))
                .andExpect(jsonPath("$.size").value(1))
                .andExpect(jsonPath("$.totalElements").value(2))
                .andExpect(jsonPath("$.totalPages").value(2));
    }

    @Test
    void getPlanById_existing_returnsPlanDetailsWithIncludedMovies() throws Exception {
        DirectorEntity director = saveDirector();
        MovieEntity movie = movieJpaRepository.save(newMovie(director));
        SubscriptionPlanEntity plan = subscriptionPlanJpaRepository.save(
                SubscriptionPlanEntity.builder()
                        .name("Premium")
                        .description("Premium plan")
                        .price(BigDecimal.valueOf(19.99))
                        .duration(30)
                        .movieIds(Set.of(movie.getId()))
                        .build()
        );

        mockMvc.perform(get("/api/subscription-plans/{id}", plan.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Premium"))
                .andExpect(jsonPath("$.includedMovies.length()").value(1))
                .andExpect(jsonPath("$.includedMovies[0].title").value("Inception"));
    }

    private SubscriptionPlanEntity savePlan(String name, BigDecimal price) {
        return subscriptionPlanJpaRepository.save(
                SubscriptionPlanEntity.builder()
                        .name(name)
                        .description(name + " plan")
                        .price(price)
                        .duration(30)
                        .movieIds(Set.of())
                        .build()
        );
    }

    private DirectorEntity saveDirector() {
        return directorJpaRepository.save(new DirectorEntity(null, "Chris", "Nolan", "bio", null));
    }

    private MovieEntity newMovie(DirectorEntity director) {
        return new MovieEntity(null, "Inception", "desc", 2010, BigDecimal.valueOf(8.8), director, null, 0L);
    }
}
