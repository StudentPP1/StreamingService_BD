package dev.studentpp1.streamingservice.payments.integration;

import dev.studentpp1.streamingservice.AbstractPostgresContainerTest;
import dev.studentpp1.streamingservice.auth.persistence.AuthenticatedUser;
import dev.studentpp1.streamingservice.payments.domain.model.PaymentStatus;
import dev.studentpp1.streamingservice.payments.infrastructure.entity.PaymentEntity;
import dev.studentpp1.streamingservice.payments.infrastructure.repository.PaymentJpaRepository;
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
import java.time.LocalDateTime;
import java.util.List;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class PaymentControllerQueryIntegrationTest extends AbstractPostgresContainerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private PaymentJpaRepository paymentJpaRepository;
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
        jdbcTemplate.execute("TRUNCATE TABLE payment, user_subscription, subscription_plan, users RESTART IDENTITY CASCADE");
    }

    private AuthenticatedUser principal() {
        return new AuthenticatedUser(
                1L,
                "ivan@example.com",
                "pass",
                List.of(new SimpleGrantedAuthority("ROLE_USER")));
    }

    private UserEntity saveUser() {
        return userJpaRepository.save(UserEntity.builder()
                .name("Ivan")
                .surname("Petrenko")
                .email("ivan@example.com")
                .password("hashed")
                .birthday(java.time.LocalDate.of(2000, 1, 1))
                .role(Role.ROLE_USER)
                .build());
    }

    @Test
    void getPaymentsByUser_unauthenticated_returnsForbidden() throws Exception {
        mockMvc.perform(get("/api/payments/user"))
                .andExpect(status().isForbidden());
    }

    @Test
    void getPaymentsByUserSubscription_unauthenticated_returnsForbidden() throws Exception {
        mockMvc.perform(get("/api/payments/user/subscription/1"))
                .andExpect(status().isForbidden());
    }

    @Test
    void getPaymentsByUser_returnsHistoryPayments() throws Exception {
        UserEntity user = saveUser();

        paymentJpaRepository.save(PaymentEntity.builder()
                .amount(BigDecimal.valueOf(9.99))
                .status(PaymentStatus.COMPLETED)
                .paidAt(LocalDateTime.now())
                .userId(user.getId())
                .productName("Basic")
                .currency("USD")
                .build());
        paymentJpaRepository.save(PaymentEntity.builder()
                .amount(BigDecimal.valueOf(19.99))
                .status(PaymentStatus.COMPLETED)
                .paidAt(LocalDateTime.now())
                .userId(user.getId())
                .productName("Premium")
                .currency("USD")
                .build());
        mockMvc.perform(get("/api/payments/user").with(user(principal())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].subscriptionName").value("Basic"))
                .andExpect(jsonPath("$[1].subscriptionName").value("Premium"));
    }

    @Test
    void getPaymentsByUserSubscription_filtersBySubscription() throws Exception {
        UserEntity user = saveUser();
        SubscriptionPlanEntity plan = subscriptionPlanJpaRepository.save(SubscriptionPlanEntity.builder()
                .name("Basic")
                .description("Basic plan")
                .price(BigDecimal.valueOf(9.99))
                .duration(30)
                .movieIds(java.util.Set.of())
                .build());
        Long sub1Id = userSubscriptionJpaRepository.save(UserSubscriptionEntity.builder()
                .user(user)
                .plan(plan)
                .startTime(LocalDateTime.now().minusDays(1))
                .endTime(LocalDateTime.now().plusDays(29))
                .status(SubscriptionStatus.ACTIVE)
                .build()).getId();
        Long sub2Id = userSubscriptionJpaRepository.save(UserSubscriptionEntity.builder()
                .user(user)
                .plan(plan)
                .startTime(LocalDateTime.now().minusDays(1))
                .endTime(LocalDateTime.now().plusDays(29))
                .status(SubscriptionStatus.ACTIVE)
                .build()).getId();
        paymentJpaRepository.save(PaymentEntity.builder()
                .amount(BigDecimal.valueOf(9.99))
                .status(PaymentStatus.COMPLETED)
                .paidAt(LocalDateTime.now())
                .userId(user.getId())
                .userSubscriptionId(sub1Id)
                .productName("Basic")
                .currency("USD")
                .build());
        paymentJpaRepository.save(PaymentEntity.builder()
                .amount(BigDecimal.valueOf(19.99))
                .status(PaymentStatus.COMPLETED)
                .paidAt(LocalDateTime.now())
                .userId(user.getId())
                .userSubscriptionId(sub2Id)
                .productName("Premium")
                .currency("USD")
                .build());
        mockMvc.perform(get("/api/payments/user/subscription/" + sub1Id).with(user(principal())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].subscriptionName").value("Basic"))
                .andExpect(jsonPath("$[0].amount").value(9.99));
    }
}

