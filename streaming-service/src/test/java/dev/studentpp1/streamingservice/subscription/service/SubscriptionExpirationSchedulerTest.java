package dev.studentpp1.streamingservice.subscription.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import dev.studentpp1.streamingservice.AbstractPostgresContainerTest;
import dev.studentpp1.streamingservice.auth.persistence.Role;
import dev.studentpp1.streamingservice.common.time.ClockService;
import dev.studentpp1.streamingservice.payments.repository.PaymentRepository;
import dev.studentpp1.streamingservice.subscription.entity.SubscriptionPlan;
import dev.studentpp1.streamingservice.subscription.entity.SubscriptionStatus;
import dev.studentpp1.streamingservice.subscription.entity.UserSubscription;
import dev.studentpp1.streamingservice.subscription.repository.SubscriptionPlanRepository;
import dev.studentpp1.streamingservice.subscription.repository.UserSubscriptionRepository;
import dev.studentpp1.streamingservice.users.entity.AppUser;
import dev.studentpp1.streamingservice.users.repository.UserRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
@DirtiesContext(classMode = ClassMode.BEFORE_CLASS)
class SubscriptionExpirationSchedulerTest extends AbstractPostgresContainerTest {

    @Autowired
    private SubscriptionExpirationScheduler subscriptionExpirationScheduler;

    @Autowired
    private UserSubscriptionRepository userSubscriptionRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SubscriptionPlanRepository subscriptionPlanRepository;

    @Autowired
    private PaymentRepository paymentRepository;

    @MockitoBean
    private ClockService clockService;

    private AppUser user1;
    private AppUser user2;
    private AppUser user3;
    private SubscriptionPlan basicPlan;
    private SubscriptionPlan standardPlan;
    private SubscriptionPlan premiumPlan;

    @BeforeEach
    void setUp() {
        String testId = UUID.randomUUID().toString().substring(0, 8);

        paymentRepository.deleteAll();
        userSubscriptionRepository.deleteAll();
        userRepository.deleteAll();

        user1 = AppUser.builder()
            .name("John")
            .surname("Doe")
            .email("john-" + testId + "@example.com")
            .password("password")
            .birthday(LocalDate.of(1990, 1, 1))
            .role(Role.ROLE_USER)
            .build();
        user1 = userRepository.save(user1);

        user2 = AppUser.builder()
            .name("Jane")
            .surname("Smith")
            .email("jane-" + testId + "@example.com")
            .password("password")
            .birthday(LocalDate.of(1995, 2, 2))
            .role(Role.ROLE_USER)
            .build();
        user2 = userRepository.save(user2);

        user3 = AppUser.builder()
            .name("Alice")
            .surname("Brown")
            .email("alice-" + testId + "@example.com")
            .password("password")
            .birthday(LocalDate.of(1998, 3, 3))
            .role(Role.ROLE_USER)
            .build();
        user3 = userRepository.save(user3);

        basicPlan = subscriptionPlanRepository.findByName("BASIC")
            .orElseThrow(() -> new IllegalStateException("BASIC plan not found"));

        standardPlan = subscriptionPlanRepository.findByName("STANDARD")
            .orElseThrow(() -> new IllegalStateException("STANDARD plan not found"));

        premiumPlan = subscriptionPlanRepository.findByName("PREMIUM")
            .orElseThrow(() -> new IllegalStateException("PREMIUM plan not found"));
    }

    // Basic expiration functionality

    @Test
    void expireSubscriptions_expiresOverdueActiveSubscriptions() {
        LocalDateTime now = LocalDateTime.of(2025, 6, 1, 12, 0);
        when(clockService.now()).thenReturn(now);

        UserSubscription overdueSubscription = UserSubscription.builder()
            .user(user1)
            .plan(basicPlan)
            .startTime(LocalDateTime.of(2025, 1, 1, 0, 0))
            .endTime(LocalDateTime.of(2025, 5, 31, 23, 59))
            .status(SubscriptionStatus.ACTIVE)
            .build();

        userSubscriptionRepository.save(overdueSubscription);

        subscriptionExpirationScheduler.expireSubscriptions();

        UserSubscription updated = userSubscriptionRepository.findById(overdueSubscription.getId())
            .orElseThrow();
        assertThat(updated.getStatus()).isEqualTo(SubscriptionStatus.EXPIRED);
    }

    @Test
    void expireSubscriptions_returnsCorrectCount() {
        LocalDateTime now = LocalDateTime.of(2025, 6, 1, 12, 0);
        when(clockService.now()).thenReturn(now);

        UserSubscription sub1 = UserSubscription.builder()
            .user(user1)
            .plan(basicPlan)
            .startTime(LocalDateTime.of(2025, 1, 1, 0, 0))
            .endTime(LocalDateTime.of(2025, 5, 15, 0, 0))
            .status(SubscriptionStatus.ACTIVE)
            .build();

        UserSubscription sub2 = UserSubscription.builder()
            .user(user2)
            .plan(standardPlan)
            .startTime(LocalDateTime.of(2025, 1, 1, 0, 0))
            .endTime(LocalDateTime.of(2025, 5, 20, 0, 0))
            .status(SubscriptionStatus.ACTIVE)
            .build();

        UserSubscription sub3 = UserSubscription.builder()
            .user(user3)
            .plan(premiumPlan)
            .startTime(LocalDateTime.of(2025, 1, 1, 0, 0))
            .endTime(LocalDateTime.of(2025, 5, 25, 0, 0))
            .status(SubscriptionStatus.ACTIVE)
            .build();

        userSubscriptionRepository.saveAll(List.of(sub1, sub2, sub3));

        subscriptionExpirationScheduler.expireSubscriptions();

        List<UserSubscription> expiredSubscriptions = userSubscriptionRepository
            .findAll()
            .stream()
            .filter(s -> s.getStatus() == SubscriptionStatus.EXPIRED)
            .toList();

        assertThat(expiredSubscriptions).hasSize(3);
    }

    @Test
    void expireSubscriptions_doesNotExpireNonOverdueSubscriptions() {
        LocalDateTime now = LocalDateTime.of(2025, 6, 1, 12, 0);
        when(clockService.now()).thenReturn(now);

        UserSubscription futureSubscription = UserSubscription.builder()
            .user(user1)
            .plan(basicPlan)
            .startTime(LocalDateTime.of(2025, 6, 1, 0, 0))
            .endTime(LocalDateTime.of(2025, 12, 31, 23, 59))
            .status(SubscriptionStatus.ACTIVE)
            .build();

        userSubscriptionRepository.save(futureSubscription);

        subscriptionExpirationScheduler.expireSubscriptions();

        UserSubscription unchanged = userSubscriptionRepository.findById(futureSubscription.getId())
            .orElseThrow();
        assertThat(unchanged.getStatus()).isEqualTo(SubscriptionStatus.ACTIVE);
    }

    @Test
    void expireSubscriptions_doesNotExpireAlreadyExpiredSubscriptions() {
        LocalDateTime now = LocalDateTime.of(2025, 6, 1, 12, 0);
        when(clockService.now()).thenReturn(now);

        UserSubscription alreadyExpired = UserSubscription.builder()
            .user(user1)
            .plan(basicPlan)
            .startTime(LocalDateTime.of(2024, 1, 1, 0, 0))
            .endTime(LocalDateTime.of(2024, 12, 31, 0, 0))
            .status(SubscriptionStatus.EXPIRED)
            .build();

        userSubscriptionRepository.save(alreadyExpired);

        subscriptionExpirationScheduler.expireSubscriptions();

        UserSubscription unchanged = userSubscriptionRepository.findById(alreadyExpired.getId())
            .orElseThrow();
        assertThat(unchanged.getStatus()).isEqualTo(SubscriptionStatus.EXPIRED);
    }

    @Test
    void expireSubscriptions_doesNotExpireCancelledSubscriptions() {
        LocalDateTime now = LocalDateTime.of(2025, 6, 1, 12, 0);
        when(clockService.now()).thenReturn(now);

        UserSubscription cancelled = UserSubscription.builder()
            .user(user1)
            .plan(basicPlan)
            .startTime(LocalDateTime.of(2025, 1, 1, 0, 0))
            .endTime(LocalDateTime.of(2025, 5, 31, 0, 0))
            .status(SubscriptionStatus.CANCELLED)
            .build();

        userSubscriptionRepository.save(cancelled);

        subscriptionExpirationScheduler.expireSubscriptions();

        UserSubscription unchanged = userSubscriptionRepository.findById(cancelled.getId())
            .orElseThrow();
        assertThat(unchanged.getStatus()).isEqualTo(SubscriptionStatus.CANCELLED);
    }

    @Test
    void expireSubscriptions_commitsChangesToDatabase() {
        LocalDateTime now = LocalDateTime.of(2025, 6, 1, 12, 0);
        when(clockService.now()).thenReturn(now);

        UserSubscription overdueSubscription = UserSubscription.builder()
            .user(user1)
            .plan(basicPlan)
            .startTime(LocalDateTime.of(2025, 1, 1, 0, 0))
            .endTime(LocalDateTime.of(2025, 5, 31, 0, 0))
            .status(SubscriptionStatus.ACTIVE)
            .build();

        userSubscriptionRepository.save(overdueSubscription);

        subscriptionExpirationScheduler.expireSubscriptions();

        UserSubscription reloaded = userSubscriptionRepository.findById(overdueSubscription.getId())
            .orElseThrow();
        assertThat(reloaded.getStatus()).isEqualTo(SubscriptionStatus.EXPIRED);

        long expiredCount = userSubscriptionRepository.findAll()
            .stream()
            .filter(s -> s.getStatus() == SubscriptionStatus.EXPIRED)
            .count();
        assertThat(expiredCount).isEqualTo(1);
    }

    @Test
    void expireSubscriptions_handlesMultipleSubscriptionsInSingleTransaction() {
        LocalDateTime now = LocalDateTime.of(2025, 6, 1, 12, 0);
        when(clockService.now()).thenReturn(now);

        List<UserSubscription> subscriptions = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            AppUser user = AppUser.builder()
                .name("User" + i)
                .surname("Test")
                .email("user" + i + "@test.com")
                .password("password")
                .birthday(LocalDate.of(1990, 1, 1))
                .role(Role.ROLE_USER)
                .build();
            user = userRepository.save(user);

            UserSubscription sub = UserSubscription.builder()
                .user(user)
                .plan(basicPlan)
                .startTime(LocalDateTime.of(2025, 1, 1, 0, 0))
                .endTime(LocalDateTime.of(2025, 5, 15, 0, 0))
                .status(SubscriptionStatus.ACTIVE)
                .build();
            subscriptions.add(sub);
        }

        userSubscriptionRepository.saveAll(subscriptions);

        subscriptionExpirationScheduler.expireSubscriptions();

        long expiredCount = userSubscriptionRepository.findAll()
            .stream()
            .filter(s -> s.getStatus() == SubscriptionStatus.EXPIRED)
            .count();

        assertThat(expiredCount).isEqualTo(5);
    }

    @Test
    void expireSubscriptions_withNoExpiredSubscriptions_returnsZero() {
        LocalDateTime now = LocalDateTime.of(2025, 6, 1, 12, 0);
        when(clockService.now()).thenReturn(now);

        UserSubscription futureSubscription = UserSubscription.builder()
            .user(user1)
            .plan(basicPlan)
            .startTime(LocalDateTime.of(2025, 6, 1, 0, 0))
            .endTime(LocalDateTime.of(2025, 12, 31, 0, 0))
            .status(SubscriptionStatus.ACTIVE)
            .build();

        userSubscriptionRepository.save(futureSubscription);

        subscriptionExpirationScheduler.expireSubscriptions();

        long expiredCount = userSubscriptionRepository.findAll()
            .stream()
            .filter(s -> s.getStatus() == SubscriptionStatus.EXPIRED)
            .count();

        assertThat(expiredCount).isEqualTo(0);

        UserSubscription unchanged = userSubscriptionRepository.findById(futureSubscription.getId())
            .orElseThrow();
        assertThat(unchanged.getStatus()).isEqualTo(SubscriptionStatus.ACTIVE);
    }

    // Bulk update operations

    @Test
    void expireSubscriptions_handlesLargeNumberOfSubscriptions() {
        LocalDateTime now = LocalDateTime.of(2025, 6, 1, 12, 0);
        when(clockService.now()).thenReturn(now);

        List<UserSubscription> subscriptions = new ArrayList<>();
        for (int i = 0; i < 500; i++) {
            AppUser user = AppUser.builder()
                .name("User" + i)
                .surname("Bulk")
                .email("bulk" + i + "@test.com")
                .password("password")
                .birthday(LocalDate.of(1990, 1, 1))
                .role(Role.ROLE_USER)
                .build();
            user = userRepository.save(user);

            UserSubscription sub = UserSubscription.builder()
                .user(user)
                .plan(basicPlan)
                .startTime(LocalDateTime.of(2025, 1, 1, 0, 0))
                .endTime(LocalDateTime.of(2025, 5, 15, 0, 0))
                .status(SubscriptionStatus.ACTIVE)
                .build();
            subscriptions.add(sub);
        }

        userSubscriptionRepository.saveAll(subscriptions);

        subscriptionExpirationScheduler.expireSubscriptions();

        long expiredCount = userSubscriptionRepository.findAll()
            .stream()
            .filter(s -> s.getStatus() == SubscriptionStatus.EXPIRED)
            .count();

        assertThat(expiredCount).isEqualTo(500);
    }

    @Test
    void expireSubscriptions_updatesDatabaseDirectly_notThroughEntityManager() {
        LocalDateTime now = LocalDateTime.of(2025, 6, 1, 12, 0);
        when(clockService.now()).thenReturn(now);

        UserSubscription overdueSubscription = UserSubscription.builder()
            .user(user1)
            .plan(basicPlan)
            .startTime(LocalDateTime.of(2025, 1, 1, 0, 0))
            .endTime(LocalDateTime.of(2025, 5, 31, 0, 0))
            .status(SubscriptionStatus.ACTIVE)
            .build();

        Long subscriptionId = userSubscriptionRepository.save(overdueSubscription).getId();

        subscriptionExpirationScheduler.expireSubscriptions();

        UserSubscription reloaded = userSubscriptionRepository.findById(subscriptionId)
            .orElseThrow();
        assertThat(reloaded.getStatus()).isEqualTo(SubscriptionStatus.EXPIRED);
    }

    // Edge cases

    @Test
    void expireSubscriptions_withExactEndTimeMatch_expiresSubscription() {
        LocalDateTime now = LocalDateTime.of(2025, 6, 1, 12, 0, 0);
        when(clockService.now()).thenReturn(now);

        UserSubscription exactMatch = UserSubscription.builder()
            .user(user1)
            .plan(basicPlan)
            .startTime(LocalDateTime.of(2025, 1, 1, 0, 0))
            .endTime(LocalDateTime.of(2025, 6, 1, 11, 59, 59))
            .status(SubscriptionStatus.ACTIVE)
            .build();

        userSubscriptionRepository.save(exactMatch);

        subscriptionExpirationScheduler.expireSubscriptions();

        UserSubscription updated = userSubscriptionRepository.findById(exactMatch.getId())
            .orElseThrow();
        assertThat(updated.getStatus()).isEqualTo(SubscriptionStatus.EXPIRED);
    }

    @Test
    void expireSubscriptions_withVariousStatuses_onlyExpiresActive() {
        LocalDateTime now = LocalDateTime.of(2025, 6, 1, 12, 0);
        when(clockService.now()).thenReturn(now);

        UserSubscription activeOverdue = UserSubscription.builder()
            .user(user1)
            .plan(basicPlan)
            .startTime(LocalDateTime.of(2025, 1, 1, 0, 0))
            .endTime(LocalDateTime.of(2025, 5, 31, 0, 0))
            .status(SubscriptionStatus.ACTIVE)
            .build();

        UserSubscription expiredOverdue = UserSubscription.builder()
            .user(user2)
            .plan(standardPlan)
            .startTime(LocalDateTime.of(2025, 1, 1, 0, 0))
            .endTime(LocalDateTime.of(2025, 5, 31, 0, 0))
            .status(SubscriptionStatus.EXPIRED)
            .build();

        UserSubscription cancelledOverdue = UserSubscription.builder()
            .user(user3)
            .plan(premiumPlan)
            .startTime(LocalDateTime.of(2025, 1, 1, 0, 0))
            .endTime(LocalDateTime.of(2025, 5, 31, 0, 0))
            .status(SubscriptionStatus.CANCELLED)
            .build();

        userSubscriptionRepository.saveAll(
            List.of(activeOverdue, expiredOverdue, cancelledOverdue));

        subscriptionExpirationScheduler.expireSubscriptions();

        UserSubscription updated1 = userSubscriptionRepository.findById(activeOverdue.getId())
            .orElseThrow();
        UserSubscription updated2 = userSubscriptionRepository.findById(expiredOverdue.getId())
            .orElseThrow();
        UserSubscription updated3 = userSubscriptionRepository.findById(cancelledOverdue.getId())
            .orElseThrow();

        assertThat(updated1.getStatus()).isEqualTo(SubscriptionStatus.EXPIRED);
        assertThat(updated2.getStatus()).isEqualTo(SubscriptionStatus.EXPIRED);
        assertThat(updated3.getStatus()).isEqualTo(SubscriptionStatus.CANCELLED);
    }

    @Test
    void expireSubscriptions_preservesOtherSubscriptionData() {
        LocalDateTime now = LocalDateTime.of(2025, 6, 1, 12, 0);
        when(clockService.now()).thenReturn(now);

        LocalDateTime startTime = LocalDateTime.of(2025, 1, 1, 10, 30);
        LocalDateTime endTime = LocalDateTime.of(2025, 5, 31, 15, 45);

        UserSubscription subscription = UserSubscription.builder()
            .user(user1)
            .plan(basicPlan)
            .startTime(startTime)
            .endTime(endTime)
            .status(SubscriptionStatus.ACTIVE)
            .build();

        Long subscriptionId = userSubscriptionRepository.save(subscription).getId();

        subscriptionExpirationScheduler.expireSubscriptions();

        UserSubscription updated = userSubscriptionRepository.findById(subscriptionId)
            .orElseThrow();
        assertThat(updated.getStatus()).isEqualTo(SubscriptionStatus.EXPIRED);
        assertThat(updated.getStartTime()).isEqualTo(startTime);
        assertThat(updated.getEndTime()).isEqualTo(endTime);
        assertThat(updated.getUser().getId()).isEqualTo(user1.getId());
        assertThat(updated.getPlan().getId()).isEqualTo(basicPlan.getId());
    }

    @Test
    void expireSubscriptions_usesClockServiceForCurrentTime() {
        LocalDateTime mockedTime = LocalDateTime.of(2025, 7, 15, 14, 30);
        when(clockService.now()).thenReturn(mockedTime);

        UserSubscription beforeMockedTime = UserSubscription.builder()
            .user(user1)
            .plan(basicPlan)
            .startTime(LocalDateTime.of(2025, 1, 1, 0, 0))
            .endTime(LocalDateTime.of(2025, 7, 14, 0, 0))
            .status(SubscriptionStatus.ACTIVE)
            .build();

        UserSubscription afterMockedTime = UserSubscription.builder()
            .user(user2)
            .plan(standardPlan)
            .startTime(LocalDateTime.of(2025, 1, 1, 0, 0))
            .endTime(LocalDateTime.of(2025, 7, 16, 0, 0))
            .status(SubscriptionStatus.ACTIVE)
            .build();

        userSubscriptionRepository.saveAll(List.of(beforeMockedTime, afterMockedTime));

        subscriptionExpirationScheduler.expireSubscriptions();

        UserSubscription expired = userSubscriptionRepository.findById(beforeMockedTime.getId())
            .orElseThrow();
        UserSubscription active = userSubscriptionRepository.findById(afterMockedTime.getId())
            .orElseThrow();

        assertThat(expired.getStatus()).isEqualTo(SubscriptionStatus.EXPIRED);
        assertThat(active.getStatus()).isEqualTo(SubscriptionStatus.ACTIVE);
    }

    @Test
    void expireSubscriptions_withMockedTime_expiresBasedOnGivenTime() {
        LocalDateTime specificTime = LocalDateTime.of(2025, 12, 25, 23, 59, 59);
        when(clockService.now()).thenReturn(specificTime);

        UserSubscription endedYesterday = UserSubscription.builder()
            .user(user1)
            .plan(basicPlan)
            .startTime(LocalDateTime.of(2025, 11, 25, 0, 0))
            .endTime(LocalDateTime.of(2025, 12, 24, 23, 59, 59))
            .status(SubscriptionStatus.ACTIVE)
            .build();

        UserSubscription endsTomorrow = UserSubscription.builder()
            .user(user2)
            .plan(standardPlan)
            .startTime(LocalDateTime.of(2025, 11, 26, 0, 0))
            .endTime(LocalDateTime.of(2025, 12, 26, 0, 0, 1))
            .status(SubscriptionStatus.ACTIVE)
            .build();

        userSubscriptionRepository.saveAll(List.of(endedYesterday, endsTomorrow));

        subscriptionExpirationScheduler.expireSubscriptions();

        UserSubscription updated1 = userSubscriptionRepository.findById(endedYesterday.getId())
            .orElseThrow();
        UserSubscription updated2 = userSubscriptionRepository.findById(endsTomorrow.getId())
            .orElseThrow();

        assertThat(updated1.getStatus()).isEqualTo(SubscriptionStatus.EXPIRED);
        assertThat(updated2.getStatus()).isEqualTo(SubscriptionStatus.ACTIVE);
    }
}

