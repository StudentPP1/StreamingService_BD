package dev.studentpp1.streamingservice.subscription.integration;

import dev.studentpp1.streamingservice.AbstractPostgresContainerTest;
import dev.studentpp1.streamingservice.subscription.infrastructure.entity.SubscriptionPlanEntity;
import dev.studentpp1.streamingservice.subscription.infrastructure.repository.SubscriptionPlanJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Set;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class SubscriptionPlanControllerQueryIntegrationTest extends AbstractPostgresContainerTest {

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
    void getPlanById_returnsDetails() throws Exception {
        SubscriptionPlanEntity plan = subscriptionPlanJpaRepository.save(
                SubscriptionPlanEntity.builder()
                        .name("Premium")
                        .description("Premium plan")
                        .price(BigDecimal.valueOf(19.99))
                        .duration(30)
                        .movieIds(Set.of())
                        .build()
        );

        mockMvc.perform(get("/api/subscription-plans/{id}", plan.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(plan.getId()))
                .andExpect(jsonPath("$.name").value("Premium"));
    }
}
