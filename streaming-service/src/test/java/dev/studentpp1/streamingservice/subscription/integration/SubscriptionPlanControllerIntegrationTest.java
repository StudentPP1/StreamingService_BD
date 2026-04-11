package dev.studentpp1.streamingservice.subscription.integration;

import dev.studentpp1.streamingservice.AbstractPostgresContainerTest;
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

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private SubscriptionPlanJpaRepository subscriptionPlanJpaRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUp() {
        jdbcTemplate.execute("TRUNCATE TABLE included_movie, user_subscription, subscription_plan RESTART IDENTITY CASCADE");
    }

    @Test
    void getAllPlans_noAuth_returnsOk() throws Exception {
        subscriptionPlanJpaRepository.save(
                SubscriptionPlanEntity.builder()
                        .name("Basic")
                        .description("Basic plan")
                        .price(BigDecimal.valueOf(9.99))
                        .duration(30)
                        .movieIds(Set.of())
                        .build()
        );

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
        String json = """
                {"name":"Basic","description":"Basic plan","price":9.99,"duration":30}
                """;

        mockMvc.perform(post("/api/subscription-plans")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createPlan_admin_returnsCreated() throws Exception {
        String json = """
                {"name":"Basic","description":"Basic plan","price":9.99,"duration":30}
                """;

        mockMvc.perform(post("/api/subscription-plans")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Basic"))
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deletePlan_notFound_returns404() throws Exception {

        mockMvc.perform(delete("/api/subscription-plans/99999"))
                .andExpect(status().isNotFound());
    }
}
