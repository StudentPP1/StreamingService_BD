package dev.studentpp1.streamingservice.subscription.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import dev.studentpp1.streamingservice.AbstractPostgresContainerTest;
import dev.studentpp1.streamingservice.auth.persistence.Role;
import dev.studentpp1.streamingservice.payments.repository.PaymentRepository;
import dev.studentpp1.streamingservice.subscription.entity.SubscriptionPlan;
import dev.studentpp1.streamingservice.subscription.entity.SubscriptionStatus;
import dev.studentpp1.streamingservice.subscription.entity.UserSubscription;
import dev.studentpp1.streamingservice.users.entity.AppUser;
import dev.studentpp1.streamingservice.users.repository.UserRepository;
import jakarta.validation.ConstraintViolationException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

@DataJpaTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DirtiesContext(classMode = ClassMode.BEFORE_CLASS)
class UserSubscriptionRepositoryTest extends AbstractPostgresContainerTest {

    @Autowired
    private UserSubscriptionRepository userSubscriptionRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SubscriptionPlanRepository subscriptionPlanRepository;

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private PlatformTransactionManager transactionManager;

    private AppUser user1;
    private AppUser user2;
    private AppUser user3;
    private SubscriptionPlan basicPlan;
    private SubscriptionPlan standardPlan;
    private SubscriptionPlan premiumPlan;

    @BeforeEach
    void setUp() {
        paymentRepository.deleteAll();
        userSubscriptionRepository.deleteAll();
        userRepository.deleteAll();

        user1 = AppUser.builder()
            .name("John")
            .surname("Doe")
            .email("john.doe@example.com")
            .password("password1")
            .birthday(LocalDate.of(1990, 1, 1))
            .role(Role.ROLE_USER)
            .build();
        user1 = userRepository.save(user1);

        user2 = AppUser.builder()
            .name("Jane")
            .surname("Smith")
            .email("jane.smith@example.com")
            .password("password2")
            .birthday(LocalDate.of(1995, 2, 2))
            .role(Role.ROLE_USER)
            .build();
        user2 = userRepository.save(user2);

        user3 = AppUser.builder()
            .name("Alice")
            .surname("Brown")
            .email("alice.brown@example.com")
            .password("password3")
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

    // Basic repository operations

    @Test
    void saveUserSubscription_persistsToDatabase() {
        LocalDateTime startTime = LocalDateTime.of(2025, 1, 1, 0, 0);
        LocalDateTime endTime = LocalDateTime.of(2025, 12, 31, 23, 59);

        UserSubscription subscription = UserSubscription.builder()
            .user(user1)
            .plan(basicPlan)
            .startTime(startTime)
            .endTime(endTime)
            .status(SubscriptionStatus.ACTIVE)
            .build();

        UserSubscription saved = userSubscriptionRepository.save(subscription);
        entityManager.flush();
        entityManager.clear();

        assertThat(saved.getId()).isNotNull();

        UserSubscription found = userSubscriptionRepository.findById(saved.getId()).orElseThrow();
        assertThat(found.getId()).isEqualTo(saved.getId());
        assertThat(found.getUser().getId()).isEqualTo(user1.getId());
        assertThat(found.getPlan().getId()).isEqualTo(basicPlan.getId());
        assertThat(found.getStatus()).isEqualTo(SubscriptionStatus.ACTIVE);
        assertThat(found.getStartTime()).isEqualTo(startTime);
        assertThat(found.getEndTime()).isEqualTo(endTime);
    }

    @Test
    void findById_returnsSubscriptionWithAllFields() {
        LocalDateTime startTime = LocalDateTime.of(2025, 6, 15, 10, 30);
        LocalDateTime endTime = LocalDateTime.of(2025, 7, 15, 10, 30);

        UserSubscription subscription = UserSubscription.builder()
            .user(user2)
            .plan(premiumPlan)
            .startTime(startTime)
            .endTime(endTime)
            .status(SubscriptionStatus.ACTIVE)
            .build();

        UserSubscription saved = userSubscriptionRepository.save(subscription);
        entityManager.flush();
        entityManager.clear();

        Optional<UserSubscription> result = userSubscriptionRepository.findById(saved.getId());

        assertThat(result).isPresent();
        UserSubscription found = result.get();
        assertThat(found.getUser().getEmail()).isEqualTo("jane.smith@example.com");
        assertThat(found.getPlan().getName()).isEqualTo("PREMIUM");
        assertThat(found.getStatus()).isEqualTo(SubscriptionStatus.ACTIVE);
        assertThat(found.getStartTime()).isEqualTo(startTime);
        assertThat(found.getEndTime()).isEqualTo(endTime);
    }

    @Test
    void findByUser_returnsAllUserSubscriptions() {
        UserSubscription sub1 = UserSubscription.builder()
            .user(user1)
            .plan(basicPlan)
            .startTime(LocalDateTime.of(2024, 1, 1, 0, 0))
            .endTime(LocalDateTime.of(2024, 12, 31, 0, 0))
            .status(SubscriptionStatus.EXPIRED)
            .build();

        UserSubscription sub2 = UserSubscription.builder()
            .user(user1)
            .plan(standardPlan)
            .startTime(LocalDateTime.of(2025, 1, 1, 0, 0))
            .endTime(LocalDateTime.of(2025, 12, 31, 0, 0))
            .status(SubscriptionStatus.ACTIVE)
            .build();

        UserSubscription sub3 = UserSubscription.builder()
            .user(user2)
            .plan(premiumPlan)
            .startTime(LocalDateTime.of(2025, 1, 1, 0, 0))
            .endTime(LocalDateTime.of(2025, 12, 31, 0, 0))
            .status(SubscriptionStatus.ACTIVE)
            .build();

        userSubscriptionRepository.saveAll(List.of(sub1, sub2, sub3));
        entityManager.flush();

        List<UserSubscription> user1Subscriptions = userSubscriptionRepository.findByUser(user1);
        List<UserSubscription> user2Subscriptions = userSubscriptionRepository.findByUser(user2);

        assertThat(user1Subscriptions).hasSize(2);
        assertThat(user1Subscriptions)
            .extracting(UserSubscription::getStatus)
            .containsExactlyInAnyOrder(SubscriptionStatus.EXPIRED, SubscriptionStatus.ACTIVE);

        assertThat(user2Subscriptions).hasSize(1);
        assertThat(user2Subscriptions.getFirst().getStatus()).isEqualTo(SubscriptionStatus.ACTIVE);
    }

    // Custom query methods

    @Test
    void findAllByUser_returnsPagedResults() {
        for (int i = 0; i < 15; i++) {
            UserSubscription sub = UserSubscription.builder()
                .user(user1)
                .plan(basicPlan)
                .startTime(LocalDateTime.of(2025, 1, i + 1, 0, 0))
                .endTime(LocalDateTime.of(2025, 2, i + 1, 0, 0))
                .status(SubscriptionStatus.ACTIVE)
                .build();
            userSubscriptionRepository.save(sub);
        }
        entityManager.flush();

        Pageable pageable = PageRequest.of(0, 10);
        Page<UserSubscription> page = userSubscriptionRepository.findAllByUser(user1, pageable);

        assertThat(page.getContent()).hasSize(10);
        assertThat(page.getTotalElements()).isEqualTo(15);
        assertThat(page.getTotalPages()).isEqualTo(2);
        assertThat(page.getNumber()).isEqualTo(0);
    }

    @Test
    void findAllByStatusAndEndTimeBefore_returnsExpiredActiveSubscriptions() {
        LocalDateTime cutoffDate = LocalDateTime.of(2025, 6, 1, 0, 0);

        UserSubscription expiredActive1 = UserSubscription.builder()
            .user(user1)
            .plan(basicPlan)
            .startTime(LocalDateTime.of(2024, 1, 1, 0, 0))
            .endTime(LocalDateTime.of(2025, 5, 15, 0, 0))
            .status(SubscriptionStatus.ACTIVE)
            .build();

        UserSubscription expiredActive2 = UserSubscription.builder()
            .user(user2)
            .plan(standardPlan)
            .startTime(LocalDateTime.of(2024, 1, 1, 0, 0))
            .endTime(LocalDateTime.of(2025, 5, 20, 0, 0))
            .status(SubscriptionStatus.ACTIVE)
            .build();

        UserSubscription stillActive = UserSubscription.builder()
            .user(user3)
            .plan(premiumPlan)
            .startTime(LocalDateTime.of(2025, 1, 1, 0, 0))
            .endTime(LocalDateTime.of(2025, 12, 31, 0, 0))
            .status(SubscriptionStatus.ACTIVE)
            .build();

        UserSubscription alreadyCancelled = UserSubscription.builder()
            .user(user1)
            .plan(standardPlan)
            .startTime(LocalDateTime.of(2024, 1, 1, 0, 0))
            .endTime(LocalDateTime.of(2025, 5, 10, 0, 0))
            .status(SubscriptionStatus.CANCELLED)
            .build();

        userSubscriptionRepository.saveAll(
            List.of(expiredActive1, expiredActive2, stillActive, alreadyCancelled));
        entityManager.flush();

        List<UserSubscription> result = userSubscriptionRepository
            .findAllByStatusAndEndTimeBefore(SubscriptionStatus.ACTIVE, cutoffDate);

        assertThat(result).hasSize(2);
        assertThat(result)
            .extracting(UserSubscription::getUser)
            .extracting(AppUser::getId)
            .containsExactlyInAnyOrder(user1.getId(), user2.getId());
    }

    @Test
    void expireOverdueSubscriptions_updatesMultipleRecords_returnsCount() {
        LocalDateTime now = LocalDateTime.of(2025, 6, 1, 0, 0);

        UserSubscription overdue1 = UserSubscription.builder()
            .user(user1)
            .plan(basicPlan)
            .startTime(LocalDateTime.of(2025, 1, 1, 0, 0))
            .endTime(LocalDateTime.of(2025, 5, 15, 0, 0))
            .status(SubscriptionStatus.ACTIVE)
            .build();

        UserSubscription overdue2 = UserSubscription.builder()
            .user(user2)
            .plan(standardPlan)
            .startTime(LocalDateTime.of(2025, 1, 1, 0, 0))
            .endTime(LocalDateTime.of(2025, 5, 20, 0, 0))
            .status(SubscriptionStatus.ACTIVE)
            .build();

        UserSubscription notOverdue = UserSubscription.builder()
            .user(user3)
            .plan(premiumPlan)
            .startTime(LocalDateTime.of(2025, 1, 1, 0, 0))
            .endTime(LocalDateTime.of(2025, 12, 31, 0, 0))
            .status(SubscriptionStatus.ACTIVE)
            .build();

        userSubscriptionRepository.saveAll(List.of(overdue1, overdue2, notOverdue));
        entityManager.flush();
        entityManager.clear();

        int updatedCount = userSubscriptionRepository.expireOverdueSubscriptions(now);
        entityManager.flush();
        entityManager.clear();

        assertThat(updatedCount).isEqualTo(2);

        UserSubscription updated1 = userSubscriptionRepository.findById(overdue1.getId())
            .orElseThrow();
        UserSubscription updated2 = userSubscriptionRepository.findById(overdue2.getId())
            .orElseThrow();
        UserSubscription unchanged = userSubscriptionRepository.findById(notOverdue.getId())
            .orElseThrow();

        assertThat(updated1.getStatus()).isEqualTo(SubscriptionStatus.EXPIRED);
        assertThat(updated2.getStatus()).isEqualTo(SubscriptionStatus.EXPIRED);
        assertThat(unchanged.getStatus()).isEqualTo(SubscriptionStatus.ACTIVE);
    }

    // Transaction behavior and locking

    @Test
    void cancelAllByPlan_cancelsAllActiveSubscriptionsForPlan() {
        UserSubscription basicActive1 = UserSubscription.builder()
            .user(user1)
            .plan(basicPlan)
            .startTime(LocalDateTime.of(2025, 1, 1, 0, 0))
            .endTime(LocalDateTime.of(2025, 12, 31, 0, 0))
            .status(SubscriptionStatus.ACTIVE)
            .build();

        UserSubscription basicActive2 = UserSubscription.builder()
            .user(user2)
            .plan(basicPlan)
            .startTime(LocalDateTime.of(2025, 1, 1, 0, 0))
            .endTime(LocalDateTime.of(2025, 12, 31, 0, 0))
            .status(SubscriptionStatus.ACTIVE)
            .build();

        UserSubscription standardActive = UserSubscription.builder()
            .user(user3)
            .plan(standardPlan)
            .startTime(LocalDateTime.of(2025, 1, 1, 0, 0))
            .endTime(LocalDateTime.of(2025, 12, 31, 0, 0))
            .status(SubscriptionStatus.ACTIVE)
            .build();

        UserSubscription basicExpired = UserSubscription.builder()
            .user(user3)
            .plan(basicPlan)
            .startTime(LocalDateTime.of(2024, 1, 1, 0, 0))
            .endTime(LocalDateTime.of(2024, 12, 31, 0, 0))
            .status(SubscriptionStatus.EXPIRED)
            .build();

        userSubscriptionRepository.saveAll(
            List.of(basicActive1, basicActive2, standardActive, basicExpired));
        entityManager.flush();
        entityManager.clear();

        userSubscriptionRepository.cancelAllByPlan(basicPlan);
        entityManager.flush();
        entityManager.clear();

        UserSubscription cancelled1 = userSubscriptionRepository.findById(basicActive1.getId())
            .orElseThrow();
        UserSubscription cancelled2 = userSubscriptionRepository.findById(basicActive2.getId())
            .orElseThrow();
        UserSubscription unchanged1 = userSubscriptionRepository.findById(standardActive.getId())
            .orElseThrow();
        UserSubscription unchanged2 = userSubscriptionRepository.findById(basicExpired.getId())
            .orElseThrow();

        assertThat(cancelled1.getStatus()).isEqualTo(SubscriptionStatus.CANCELLED);
        assertThat(cancelled2.getStatus()).isEqualTo(SubscriptionStatus.CANCELLED);
        assertThat(unchanged1.getStatus()).isEqualTo(SubscriptionStatus.ACTIVE);
        assertThat(unchanged2.getStatus()).isEqualTo(SubscriptionStatus.EXPIRED);
    }

    @Test
    void findByIdWithLock_appliesPessimisticWriteLock() {
        UserSubscription subscription = UserSubscription.builder()
            .user(user1)
            .plan(basicPlan)
            .startTime(LocalDateTime.of(2025, 1, 1, 0, 0))
            .endTime(LocalDateTime.of(2025, 12, 31, 0, 0))
            .status(SubscriptionStatus.ACTIVE)
            .build();

        UserSubscription saved = userSubscriptionRepository.save(subscription);
        entityManager.flush();
        entityManager.clear();

        Optional<UserSubscription> result = userSubscriptionRepository.findByIdWithLock(
            saved.getId());

        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(saved.getId());
    }

    @Test
    @Order(Integer.MAX_VALUE)
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    void concurrentCancellation_withLocking_preventsDuplicateUpdates() throws InterruptedException {
        TransactionTemplate setupTx = new TransactionTemplate(transactionManager);
        Long subscriptionId = setupTx.execute(status -> {
            UserSubscription subscription = UserSubscription.builder()
                .user(user1)
                .plan(basicPlan)
                .startTime(LocalDateTime.of(2025, 1, 1, 0, 0))
                .endTime(LocalDateTime.of(2025, 12, 31, 0, 0))
                .status(SubscriptionStatus.ACTIVE)
                .build();

            UserSubscription saved = userSubscriptionRepository.save(subscription);
            return saved.getId();
        });

        int threadCount = 3;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch doneLatch = new CountDownLatch(threadCount);
        AtomicInteger successCount = new AtomicInteger(0);

        TransactionTemplate txTemplate = new TransactionTemplate(transactionManager);
        txTemplate.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);

        for (int i = 0; i < threadCount; i++) {
            executor.submit(() -> {
                try {
                    startLatch.await();
                    txTemplate.execute(txStatus -> {
                        Optional<UserSubscription> locked = userSubscriptionRepository.findByIdWithLock(
                            subscriptionId);
                        if (locked.isPresent()
                            && locked.get().getStatus() == SubscriptionStatus.ACTIVE) {
                            locked.get().setStatus(SubscriptionStatus.CANCELLED);
                            userSubscriptionRepository.save(locked.get());
                            successCount.incrementAndGet();
                        }
                        return null;
                    });
                } catch (Exception ignored) {
                    // Expected for some threads
                } finally {
                    doneLatch.countDown();
                }
            });
        }

        startLatch.countDown();
        doneLatch.await(10, TimeUnit.SECONDS);
        executor.shutdown();

        TransactionTemplate readTx = new TransactionTemplate(transactionManager);
        readTx.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
        UserSubscription result = readTx.execute(status ->
            userSubscriptionRepository.findById(subscriptionId).orElseThrow()
        );

        assertThat(successCount.get()).isEqualTo(1);
        assertThat(result.getStatus()).isEqualTo(SubscriptionStatus.CANCELLED);
    }

    @Test
    void expireOverdueSubscriptions_worksInTransaction() {
        LocalDateTime now = LocalDateTime.of(2025, 6, 1, 0, 0);

        UserSubscription overdue1 = UserSubscription.builder()
            .user(user1)
            .plan(basicPlan)
            .startTime(LocalDateTime.of(2025, 1, 1, 0, 0))
            .endTime(LocalDateTime.of(2025, 5, 15, 0, 0))
            .status(SubscriptionStatus.ACTIVE)
            .build();

        UserSubscription overdue2 = UserSubscription.builder()
            .user(user2)
            .plan(standardPlan)
            .startTime(LocalDateTime.of(2025, 1, 1, 0, 0))
            .endTime(LocalDateTime.of(2025, 5, 20, 0, 0))
            .status(SubscriptionStatus.ACTIVE)
            .build();

        userSubscriptionRepository.saveAll(List.of(overdue1, overdue2));
        entityManager.flush();

        int updateCount = userSubscriptionRepository.expireOverdueSubscriptions(now);
        entityManager.flush();
        entityManager.clear();

        assertThat(updateCount).isEqualTo(2);

        List<UserSubscription> allSubscriptions = userSubscriptionRepository.findAll();
        long expiredCount = allSubscriptions.stream()
            .filter(s -> s.getStatus() == SubscriptionStatus.EXPIRED)
            .count();

        assertThat(expiredCount).isEqualTo(2);
    }

    // Entity relationships

    @Test
    void saveSubscription_withUser_maintainsRelationship() {
        UserSubscription subscription = UserSubscription.builder()
            .user(user1)
            .plan(basicPlan)
            .startTime(LocalDateTime.of(2025, 1, 1, 0, 0))
            .endTime(LocalDateTime.of(2025, 12, 31, 0, 0))
            .status(SubscriptionStatus.ACTIVE)
            .build();

        UserSubscription saved = userSubscriptionRepository.save(subscription);
        entityManager.flush();
        entityManager.clear();

        UserSubscription found = userSubscriptionRepository.findById(saved.getId()).orElseThrow();
        assertThat(found.getUser()).isNotNull();
        assertThat(found.getUser().getId()).isEqualTo(user1.getId());
        assertThat(found.getUser().getEmail()).isEqualTo("john.doe@example.com");
    }

    @Test
    void saveSubscription_withPlan_loadsEagerly() {
        UserSubscription subscription = UserSubscription.builder()
            .user(user1)
            .plan(premiumPlan)
            .startTime(LocalDateTime.of(2025, 1, 1, 0, 0))
            .endTime(LocalDateTime.of(2025, 12, 31, 0, 0))
            .status(SubscriptionStatus.ACTIVE)
            .build();

        UserSubscription saved = userSubscriptionRepository.save(subscription);
        entityManager.flush();
        entityManager.clear();

        UserSubscription found = userSubscriptionRepository.findById(saved.getId()).orElseThrow();
        assertThat(found.getPlan()).isNotNull();
        assertThat(found.getPlan().getName()).isEqualTo("PREMIUM");
        assertThat(found.getPlan().getDescription()).isNotNull();
    }

    @Test
    void findAllByUser_withEntityGraph_loadsPlansEagerly() {
        UserSubscription sub1 = UserSubscription.builder()
            .user(user1)
            .plan(basicPlan)
            .startTime(LocalDateTime.of(2025, 1, 1, 0, 0))
            .endTime(LocalDateTime.of(2025, 12, 31, 0, 0))
            .status(SubscriptionStatus.ACTIVE)
            .build();

        UserSubscription sub2 = UserSubscription.builder()
            .user(user1)
            .plan(premiumPlan)
            .startTime(LocalDateTime.of(2024, 1, 1, 0, 0))
            .endTime(LocalDateTime.of(2024, 12, 31, 0, 0))
            .status(SubscriptionStatus.EXPIRED)
            .build();

        userSubscriptionRepository.saveAll(List.of(sub1, sub2));
        entityManager.flush();
        entityManager.clear();

        Pageable pageable = PageRequest.of(0, 10);
        Page<UserSubscription> page = userSubscriptionRepository.findAllByUser(user1, pageable);

        assertThat(page.getContent()).hasSize(2);
        page.getContent().forEach(subscription -> {
            assertThat(subscription.getPlan()).isNotNull();
            assertThat(subscription.getPlan().getName()).isNotNull();
        });
    }

    // Data integrity and validation

    @Test
    void saveSubscription_withInvalidDates_failsValidation() {
        UserSubscription subscription = UserSubscription.builder()
            .user(user1)
            .plan(basicPlan)
            .startTime(LocalDateTime.of(2025, 12, 31, 0, 0))
            .endTime(LocalDateTime.of(2025, 1, 1, 0, 0))
            .status(SubscriptionStatus.ACTIVE)
            .build();

        assertThatThrownBy(() -> {
            userSubscriptionRepository.save(subscription);
            entityManager.flush();
        }).isInstanceOf(ConstraintViolationException.class);
    }

    @Test
    void saveSubscription_withNullUser_failsConstraint() {
        UserSubscription subscription = UserSubscription.builder()
            .user(null)
            .plan(basicPlan)
            .startTime(LocalDateTime.of(2025, 1, 1, 0, 0))
            .endTime(LocalDateTime.of(2025, 12, 31, 0, 0))
            .status(SubscriptionStatus.ACTIVE)
            .build();

        assertThatThrownBy(() -> {
            userSubscriptionRepository.save(subscription);
            entityManager.flush();
        }).isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    void saveSubscription_withNullPlan_failsConstraint() {
        UserSubscription subscription = UserSubscription.builder()
            .user(user1)
            .plan(null)
            .startTime(LocalDateTime.of(2025, 1, 1, 0, 0))
            .endTime(LocalDateTime.of(2025, 12, 31, 0, 0))
            .status(SubscriptionStatus.ACTIVE)
            .build();

        assertThatThrownBy(() -> {
            userSubscriptionRepository.save(subscription);
            entityManager.flush();
        }).isInstanceOf(DataIntegrityViolationException.class);
    }
}

