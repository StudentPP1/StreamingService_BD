package dev.studentpp1.streamingservice.subscription.integration;

import dev.studentpp1.streamingservice.AbstractPostgresContainerTest;
import dev.studentpp1.streamingservice.auth.persistence.AuthenticatedUser;
import dev.studentpp1.streamingservice.subscription.domain.model.SubscriptionStatus;
import dev.studentpp1.streamingservice.subscription.infrastructure.entity.SubscriptionPlanEntity;
import dev.studentpp1.streamingservice.subscription.infrastructure.entity.UserSubscriptionEntity;
import dev.studentpp1.streamingservice.subscription.infrastructure.repository.SubscriptionPlanJpaRepository;
import dev.studentpp1.streamingservice.subscription.infrastructure.repository.UserSubscriptionJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class SubscriptionControllerIntegrationTest extends AbstractPostgresContainerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private SubscriptionPlanJpaRepository subscriptionPlanJpaRepository;

    @Autowired
    private UserSubscriptionJpaRepository userSubscriptionJpaRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUp() {
        jdbcTemplate.execute("TRUNCATE TABLE included_movie, user_subscription, subscription_plan RESTART IDENTITY CASCADE");
    }

    @Test
    void getMySubscriptions_unauthenticated_returnsForbidden() throws Exception {
        mockMvc.perform(get("/api/subscriptions/my"))
                .andExpect(status().isForbidden());
    }

    @Test
    void getMySubscriptions_returnsOnlyCurrentUserSubscriptions() throws Exception {
        SubscriptionPlanEntity plan = subscriptionPlanJpaRepository.save(
                SubscriptionPlanEntity.builder()
                        .name("Basic")
                        .description("Basic plan")
                        .price(BigDecimal.valueOf(9.99))
                        .duration(30)
                        .movieIds(Set.of())
                        .build()
        );

        userSubscriptionJpaRepository.save(UserSubscriptionEntity.builder()
                .startTime(LocalDateTime.now())
                .endTime(LocalDateTime.now().plusDays(30))
                .status(SubscriptionStatus.ACTIVE)
                .plan(plan)
                .userId(1L)
                .build());

        userSubscriptionJpaRepository.save(UserSubscriptionEntity.builder()
                .startTime(LocalDateTime.now())
                .endTime(LocalDateTime.now().plusDays(30))
                .status(SubscriptionStatus.ACTIVE)
                .plan(plan)
                .userId(2L)
                .build());

        mockMvc.perform(get("/api/subscriptions/my")
                        .with(user(authenticatedUser(1L)))
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.content[0].planName").value("Basic"))
                .andExpect(jsonPath("$.content[0].subscription.userId").value(1));
    }

    @Test
    void getMySubscriptions_returnsPaginationMetadata() throws Exception {
        SubscriptionPlanEntity plan = subscriptionPlanJpaRepository.save(
                SubscriptionPlanEntity.builder()
                        .name("Basic")
                        .description("Basic plan")
                        .price(BigDecimal.valueOf(9.99))
                        .duration(30)
                        .movieIds(Set.of())
                        .build()
        );

        userSubscriptionJpaRepository.save(UserSubscriptionEntity.builder()
                .startTime(LocalDateTime.now())
                .endTime(LocalDateTime.now().plusDays(30))
                .status(SubscriptionStatus.ACTIVE)
                .plan(plan)
                .userId(1L)
                .build());

        userSubscriptionJpaRepository.save(UserSubscriptionEntity.builder()
                .startTime(LocalDateTime.now())
                .endTime(LocalDateTime.now().plusDays(30))
                .status(SubscriptionStatus.ACTIVE)
                .plan(plan)
                .userId(1L)
                .build());

        mockMvc.perform(get("/api/subscriptions/my")
                        .with(user(authenticatedUser(1L)))
                        .param("page", "0")
                        .param("size", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.page").value(0))
                .andExpect(jsonPath("$.size").value(1))
                .andExpect(jsonPath("$.totalElements").value(2))
                .andExpect(jsonPath("$.totalPages").value(2));
    }

    private AuthenticatedUser authenticatedUser(Long userId) {
        return new AuthenticatedUser(
                userId,
                "user" + userId,
                "password",
                List.of(new SimpleGrantedAuthority("ROLE_USER"))
        );
    }
}


