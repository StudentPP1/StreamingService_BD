package dev.studentpp1.streamingservice.analytics.repository;

import dev.studentpp1.streamingservice.AbstractPostgresContainerTest;
import dev.studentpp1.streamingservice.analytics.dto.MonthlyPlanStatisticProjection;
import dev.studentpp1.streamingservice.auth.persistence.Role;
import dev.studentpp1.streamingservice.payments.entity.Payment;
import dev.studentpp1.streamingservice.payments.entity.PaymentStatus;
import dev.studentpp1.streamingservice.payments.repository.PaymentRepository;
import dev.studentpp1.streamingservice.subscription.entity.SubscriptionPlan;
import dev.studentpp1.streamingservice.subscription.entity.SubscriptionStatus;
import dev.studentpp1.streamingservice.subscription.entity.UserSubscription;
import dev.studentpp1.streamingservice.subscription.repository.SubscriptionPlanRepository;
import dev.studentpp1.streamingservice.subscription.repository.UserSubscriptionRepository;
import dev.studentpp1.streamingservice.users.entity.AppUser;
import dev.studentpp1.streamingservice.users.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.test.annotation.DirtiesContext;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class AnalyticsRepositoryTest extends AbstractPostgresContainerTest {

    @Autowired
    private AnalyticsRepository analyticsRepository;

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private UserSubscriptionRepository userSubscriptionRepository;

    @Autowired
    private SubscriptionPlanRepository subscriptionPlanRepository;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        paymentRepository.deleteAll();
        userSubscriptionRepository.deleteAll();
        userRepository.deleteAll();

        AppUser user1 = AppUser.builder()
                .name("John")
                .surname("Doe")
                .email("user1@example.com")
                .password("password1")
                .birthday(LocalDate.of(1990, 1, 1))
                .role(Role.ROLE_USER)
                .build();
        user1 = userRepository.save(user1);

        AppUser user2 = AppUser.builder()
                .name("Jane")
                .surname("Smith")
                .email("user2@example.com")
                .password("password2")
                .birthday(LocalDate.of(1995, 2, 2))
                .role(Role.ROLE_USER)
                .build();
        user2 = userRepository.save(user2);

        AppUser user3 = AppUser.builder()
                .name("Alice")
                .surname("Brown")
                .email("user3@example.com")
                .password("password3")
                .birthday(LocalDate.of(1998, 3, 3))
                .role(Role.ROLE_USER)
                .build();
        user3 = userRepository.save(user3);

        // ---------- PLANS ----------
        // Use existing plans from Flyway migration instead of creating new ones
        SubscriptionPlan basic = subscriptionPlanRepository.findByName("BASIC")
                .orElseThrow(() -> new IllegalStateException("BASIC plan not found"));

        SubscriptionPlan standard = subscriptionPlanRepository.findByName("STANDARD")
                .orElseThrow(() -> new IllegalStateException("STANDARD plan not found"));

        SubscriptionPlan premium = subscriptionPlanRepository.findByName("PREMIUM")
                .orElseThrow(() -> new IllegalStateException("PREMIUM plan not found"));

        // ---------- USER SUBSCRIPTIONS ----------
        UserSubscription usUser1Basic = UserSubscription.builder()
                .user(user1)
                .plan(basic)
                .startTime(LocalDateTime.of(2025, 1, 1, 0, 0))
                .endTime(LocalDateTime.of(2025, 12, 31, 0, 0))
                .status(SubscriptionStatus.ACTIVE)
                .build();
        usUser1Basic = userSubscriptionRepository.save(usUser1Basic);

        UserSubscription usUser2Standard = UserSubscription.builder()
                .user(user2)
                .plan(standard)
                .startTime(LocalDateTime.of(2025, 1, 1, 0, 0))
                .endTime(LocalDateTime.of(2025, 12, 31, 0, 0))
                .status(SubscriptionStatus.ACTIVE)
                .build();
        usUser2Standard = userSubscriptionRepository.save(usUser2Standard);

        UserSubscription usUser3Premium = UserSubscription.builder()
                .user(user3)
                .plan(premium)
                .startTime(LocalDateTime.of(2025, 1, 1, 0, 0))
                .endTime(LocalDateTime.of(2025, 12, 31, 0, 0))
                .status(SubscriptionStatus.ACTIVE)
                .build();
        usUser3Premium = userSubscriptionRepository.save(usUser3Premium);

        // ---------- PAYMENTS: 2025-12 ----------
        paymentRepository.save(Payment.builder()
                .userSubscription(usUser1Basic)
                .status(PaymentStatus.COMPLETED)
                .paidAt(LocalDateTime.of(2025, 12, 5, 10, 0))
                .amount(BigDecimal.valueOf(100))
                .build());

        paymentRepository.save(Payment.builder()
                .userSubscription(usUser2Standard)
                .status(PaymentStatus.COMPLETED)
                .paidAt(LocalDateTime.of(2025, 12, 6, 11, 0))
                .amount(BigDecimal.valueOf(150))
                .build());

        paymentRepository.save(Payment.builder()
                .userSubscription(usUser3Premium)
                .status(PaymentStatus.COMPLETED)
                .paidAt(LocalDateTime.of(2025, 12, 7, 12, 0))
                .amount(BigDecimal.valueOf(200))
                .build());

        // ---------- PAYMENTS: 2025-11 ----------
        paymentRepository.save(Payment.builder()
                .userSubscription(usUser2Standard)
                .status(PaymentStatus.COMPLETED)
                .paidAt(LocalDateTime.of(2025, 11, 10, 10, 0))
                .amount(BigDecimal.valueOf(150))
                .build());

        paymentRepository.save(Payment.builder()
                .userSubscription(usUser3Premium)
                .status(PaymentStatus.COMPLETED)
                .paidAt(LocalDateTime.of(2025, 11, 12, 11, 0))
                .amount(BigDecimal.valueOf(200))
                .build());

        paymentRepository.save(Payment.builder()
                .userSubscription(usUser3Premium)
                .status(PaymentStatus.FAILED)
                .paidAt(LocalDateTime.of(2025, 11, 13, 12, 0))
                .amount(BigDecimal.valueOf(200))
                .build());
    }

    @Test
    void findMonthlyPlanStatistics_returnsCorrectData() {
        List<MonthlyPlanStatisticProjection> stats = analyticsRepository.findMonthlyPlanStatistics();
        assertThat(stats).hasSize(5);
    }
}