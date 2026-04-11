package dev.studentpp1.streamingservice.subscription.integration;
import dev.studentpp1.streamingservice.AbstractPostgresContainerTest;
import dev.studentpp1.streamingservice.auth.persistence.AuthenticatedUser;
import dev.studentpp1.streamingservice.subscription.domain.model.SubscriptionStatus;
import dev.studentpp1.streamingservice.subscription.infrastructure.entity.SubscriptionPlanEntity;
import dev.studentpp1.streamingservice.subscription.infrastructure.entity.UserSubscriptionEntity;
import dev.studentpp1.streamingservice.subscription.infrastructure.repository.SubscriptionPlanJpaRepository;
import dev.studentpp1.streamingservice.subscription.infrastructure.repository.UserSubscriptionJpaRepository;
import dev.studentpp1.streamingservice.users.domain.model.Role;
import dev.studentpp1.streamingservice.users.infrastructure.entity.UserEntity;
import dev.studentpp1.streamingservice.users.infrastructure.repository.UserJpaRepository;
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
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class SubscriptionControllerQueryIntegrationTest extends AbstractPostgresContainerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private UserJpaRepository userJpaRepository;
    @Autowired
    private SubscriptionPlanJpaRepository subscriptionPlanJpaRepository;
    @Autowired
    private UserSubscriptionJpaRepository userSubscriptionJpaRepository;
    @Autowired
    private JdbcTemplate jdbcTemplate;
    @BeforeEach
    void setUp() {
        jdbcTemplate.execute("TRUNCATE TABLE user_subscription, included_movie, subscription_plan, users RESTART IDENTITY CASCADE");
    }
    private AuthenticatedUser principal() {
        return new AuthenticatedUser(
                1L,
                "ivan@example.com",
                "pass",
                List.of(new SimpleGrantedAuthority("ROLE_USER")));
    }

    @Test
    void getMySubscriptions_unauthenticated_returnsForbidden() throws Exception {
        mockMvc.perform(get("/api/subscriptions/my"))
                .andExpect(status().isForbidden());
    }

    @Test
    void getMySubscriptions_returnsPageOfDtos() throws Exception {
        UserEntity user = userJpaRepository.save(UserEntity.builder()
                .name("Ivan")
                .surname("Petrenko")
                .email("ivan@example.com")
                .password("hashed")
                .birthday(LocalDate.of(2000, 1, 1))
                .role(Role.ROLE_USER)
                .build());
        SubscriptionPlanEntity plan = subscriptionPlanJpaRepository.save(SubscriptionPlanEntity.builder()
                .name("Basic")
                .description("Basic plan")
                .price(BigDecimal.valueOf(9.99))
                .duration(30)
                .movieIds(Set.of())
                .build());
        userSubscriptionJpaRepository.save(UserSubscriptionEntity.builder()
                .user(user)
                .plan(plan)
                .startTime(LocalDateTime.now().minusDays(1))
                .endTime(LocalDateTime.now().plusDays(29))
                .status(SubscriptionStatus.ACTIVE)
                .build());
        mockMvc.perform(get("/api/subscriptions/my").with(user(principal())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].planName").value("Basic"))
                .andExpect(jsonPath("$.content[0].status").value("ACTIVE"));
    }
}

