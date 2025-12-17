package dev.studentpp1.streamingservice.subscription.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import dev.studentpp1.streamingservice.AbstractPostgresContainerTest;
import dev.studentpp1.streamingservice.movies.entity.Director;
import dev.studentpp1.streamingservice.movies.entity.Movie;
import dev.studentpp1.streamingservice.movies.repository.DirectorRepository;
import dev.studentpp1.streamingservice.movies.repository.MovieRepository;
import dev.studentpp1.streamingservice.movies.repository.PerformanceRepository;
import dev.studentpp1.streamingservice.subscription.entity.SubscriptionPlan;
import jakarta.validation.ConstraintViolationException;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.support.TransactionTemplate;

@DataJpaTest
@DirtiesContext(classMode = ClassMode.BEFORE_CLASS)
class SubscriptionPlanRepositoryTest extends AbstractPostgresContainerTest {

    @Autowired
    private SubscriptionPlanRepository subscriptionPlanRepository;

    @Autowired
    private MovieRepository movieRepository;

    @Autowired
    private DirectorRepository directorRepository;

    @Autowired
    private PerformanceRepository performanceRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private PlatformTransactionManager transactionManager;

    private Movie movie1;
    private Movie movie2;
    private Movie movie3;

    @BeforeEach
    void setUp() {
        subscriptionPlanRepository.deleteAll();
        performanceRepository.deleteAll();
        movieRepository.deleteAll();
        directorRepository.deleteAll();
        entityManager.flush();
        entityManager.clear();

        Director testDirector = new Director();
        testDirector.setName("Christopher");
        testDirector.setSurname("Nolan");
        testDirector.setBiography("Famous director");
        testDirector = directorRepository.save(testDirector);
        
        movie1 = new Movie();
        movie1.setTitle("Inception");
        movie1.setDescription("A mind-bending thriller");
        movie1.setYear(2010);
        movie1.setRating(8.8);
        movie1.setDirector(testDirector);
        movie1 = movieRepository.save(movie1);

        movie2 = new Movie();
        movie2.setTitle("Interstellar");
        movie2.setDescription("Space exploration epic");
        movie2.setYear(2014);
        movie2.setRating(8.6);
        movie2.setDirector(testDirector);
        movie2 = movieRepository.save(movie2);

        movie3 = new Movie();
        movie3.setTitle("The Dark Knight");
        movie3.setDescription("Batman confronts the Joker");
        movie3.setYear(2008);
        movie3.setRating(9.0);
        movie3.setDirector(testDirector);
        movie3 = movieRepository.save(movie3);

        entityManager.flush();
        entityManager.clear();
    }

    // Basic repository operations

    @Test
    void saveSubscriptionPlan_persistsToDatabase() {
        SubscriptionPlan plan = SubscriptionPlan.builder()
            .name("GOLD")
            .description("Gold subscription plan")
            .price(new BigDecimal("49.99"))
            .duration(30)
            .movies(new HashSet<>())
            .build();

        SubscriptionPlan saved = subscriptionPlanRepository.save(plan);
        entityManager.flush();
        entityManager.clear();

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getVersion()).isNotNull();

        SubscriptionPlan found = subscriptionPlanRepository.findById(saved.getId()).orElseThrow();
        assertThat(found.getName()).isEqualTo("GOLD");
        assertThat(found.getDescription()).isEqualTo("Gold subscription plan");
        assertThat(found.getPrice()).isEqualByComparingTo("49.99");
        assertThat(found.getDuration()).isEqualTo(30);
    }

    @Test
    void findById_returnsCompleteSubscriptionPlan() {
        SubscriptionPlan plan = SubscriptionPlan.builder()
            .name("PLATINUM")
            .description("Platinum plan with all features")
            .price(new BigDecimal("99.99"))
            .duration(60)
            .movies(new HashSet<>())
            .build();

        SubscriptionPlan saved = subscriptionPlanRepository.save(plan);
        entityManager.flush();
        entityManager.clear();

        Optional<SubscriptionPlan> result = subscriptionPlanRepository.findById(saved.getId());

        assertThat(result).isPresent();
        SubscriptionPlan found = result.get();
        assertThat(found.getName()).isEqualTo("PLATINUM");
        assertThat(found.getDescription()).isEqualTo("Platinum plan with all features");
        assertThat(found.getPrice()).isEqualByComparingTo("99.99");
        assertThat(found.getDuration()).isEqualTo(60);
    }

    @Test
    void findByName_returnsCorrectPlan() {
        SubscriptionPlan plan1 = SubscriptionPlan.builder()
            .name("SILVER")
            .description("Silver plan")
            .price(new BigDecimal("29.99"))
            .duration(30)
            .movies(new HashSet<>())
            .build();

        SubscriptionPlan plan2 = SubscriptionPlan.builder()
            .name("BRONZE")
            .description("Bronze plan")
            .price(new BigDecimal("19.99"))
            .duration(30)
            .movies(new HashSet<>())
            .build();

        subscriptionPlanRepository.saveAll(List.of(plan1, plan2));
        entityManager.flush();
        entityManager.clear();

        Optional<SubscriptionPlan> result = subscriptionPlanRepository.findByName("SILVER");

        assertThat(result).isPresent();
        assertThat(result.get().getName()).isEqualTo("SILVER");
        assertThat(result.get().getPrice()).isEqualByComparingTo("29.99");
    }

    @Test
    void findAll_returnsAllNonDeletedPlans() {
        SubscriptionPlan plan1 = SubscriptionPlan.builder()
            .name("PLAN_A")
            .description("Plan A")
            .price(new BigDecimal("10.00"))
            .duration(7)
            .movies(new HashSet<>())
            .build();

        SubscriptionPlan plan2 = SubscriptionPlan.builder()
            .name("PLAN_B")
            .description("Plan B")
            .price(new BigDecimal("20.00"))
            .duration(14)
            .movies(new HashSet<>())
            .build();

        SubscriptionPlan plan3 = SubscriptionPlan.builder()
            .name("PLAN_С")
            .description("Plan С")
            .price(new BigDecimal("230.00"))
            .duration(21)
            .movies(new HashSet<>())
            .build();

        subscriptionPlanRepository.saveAll(List.of(plan1, plan2, plan3));
        subscriptionPlanRepository.delete(plan3);
        entityManager.flush();

        List<SubscriptionPlan> allPlans = subscriptionPlanRepository.findAll();

        assertThat(allPlans).hasSizeGreaterThanOrEqualTo(2);
        assertThat(allPlans)
            .extracting(SubscriptionPlan::getName)
            .contains("PLAN_A", "PLAN_B")
            .doesNotContain("PLAN_С");
    }

    // Custom query methods

    @Test
    void findAllByNameContainingIgnoreCase_returnsMatchingPlans() {
        SubscriptionPlan ultraPlan = SubscriptionPlan.builder()
            .name("ULTRA_PREMIUM")
            .description("Ultra premium plan")
            .price(new BigDecimal("199.99"))
            .duration(90)
            .movies(new HashSet<>())
            .build();

        SubscriptionPlan premiumPlus = SubscriptionPlan.builder()
            .name("PREMIUM_PLUS")
            .description("Premium plus plan")
            .price(new BigDecimal("79.99"))
            .duration(45)
            .movies(new HashSet<>())
            .build();

        SubscriptionPlan basicPlan = SubscriptionPlan.builder()
            .name("BASIC_NEW")
            .description("New basic plan")
            .price(new BigDecimal("9.99"))
            .duration(7)
            .movies(new HashSet<>())
            .build();

        subscriptionPlanRepository.saveAll(List.of(ultraPlan, premiumPlus, basicPlan));
        entityManager.flush();

        Pageable pageable = PageRequest.of(0, 10);
        Page<SubscriptionPlan> result = subscriptionPlanRepository
            .findAllByNameContainingIgnoreCase("premium", pageable);

        assertThat(result.getContent()).hasSizeGreaterThanOrEqualTo(2);
        assertThat(result.getContent())
            .extracting(SubscriptionPlan::getName)
            .anyMatch(name -> name.contains("PREMIUM"));
    }

    @Test
    void findAllByNameContainingIgnoreCase_caseInsensitiveSearch() {
        SubscriptionPlan plan = SubscriptionPlan.builder()
            .name("ELITE_PLAN")
            .description("Elite subscription")
            .price(new BigDecimal("149.99"))
            .duration(60)
            .movies(new HashSet<>())
            .build();

        subscriptionPlanRepository.save(plan);
        entityManager.flush();

        Pageable pageable = PageRequest.of(0, 10);
        Page<SubscriptionPlan> resultLower = subscriptionPlanRepository
            .findAllByNameContainingIgnoreCase("elite", pageable);
        Page<SubscriptionPlan> resultUpper = subscriptionPlanRepository
            .findAllByNameContainingIgnoreCase("ELITE", pageable);
        Page<SubscriptionPlan> resultMixed = subscriptionPlanRepository
            .findAllByNameContainingIgnoreCase("ElItE", pageable);

        assertThat(resultLower.getContent())
            .extracting(SubscriptionPlan::getName)
            .contains("ELITE_PLAN");
        assertThat(resultUpper.getContent())
            .extracting(SubscriptionPlan::getName)
            .contains("ELITE_PLAN");
        assertThat(resultMixed.getContent())
            .extracting(SubscriptionPlan::getName)
            .contains("ELITE_PLAN");
    }

    @Test
    void findAllByNameContainingIgnoreCase_withPagination() {
        for (int i = 1; i <= 15; i++) {
            SubscriptionPlan plan = SubscriptionPlan.builder()
                .name("TEST_PLAN_" + i)
                .description("Test plan " + i)
                .price(new BigDecimal("10.00"))
                .duration(7)
                .movies(new HashSet<>())
                .build();
            subscriptionPlanRepository.save(plan);
        }
        entityManager.flush();

        Pageable pageable = PageRequest.of(0, 10);
        Page<SubscriptionPlan> page = subscriptionPlanRepository
            .findAllByNameContainingIgnoreCase("test", pageable);

        assertThat(page.getContent()).hasSize(10);
        assertThat(page.getTotalElements()).isEqualTo(15);
        assertThat(page.getTotalPages()).isEqualTo(2);
    }

    @Test
    void findWithMoviesById_loadsMoviesEagerly() {
        Set<Movie> movies = new HashSet<>();
        movies.add(movie1);
        movies.add(movie2);

        SubscriptionPlan plan = SubscriptionPlan.builder()
            .name("MOVIE_PLAN")
            .description("Plan with movies")
            .price(new BigDecimal("59.99"))
            .duration(30)
            .movies(movies)
            .build();

        SubscriptionPlan saved = subscriptionPlanRepository.save(plan);
        entityManager.flush();
        entityManager.clear();

        Optional<SubscriptionPlan> result = subscriptionPlanRepository.findWithMoviesById(
            saved.getId());

        assertThat(result).isPresent();
        SubscriptionPlan found = result.get();
        assertThat(found.getMovies()).hasSize(2);
        assertThat(found.getMovies())
            .extracting(Movie::getTitle)
            .containsExactlyInAnyOrder("Inception", "Interstellar");
    }

    // Soft delete behavior

    @Test
    void deleteSubscriptionPlan_softDeletesRecord() {
        SubscriptionPlan plan = SubscriptionPlan.builder()
            .name("TO_DELETE")
            .description("Plan to be deleted")
            .price(new BigDecimal("39.99"))
            .duration(30)
            .movies(new HashSet<>())
            .build();

        SubscriptionPlan saved = subscriptionPlanRepository.save(plan);
        Long planId = saved.getId();
        entityManager.flush();
        entityManager.clear();

        subscriptionPlanRepository.deleteById(planId);
        entityManager.flush();
        entityManager.clear();

        Optional<SubscriptionPlan> result = subscriptionPlanRepository.findById(planId);
        assertThat(result).isEmpty();

        Long count = ((Number) entityManager.getEntityManager()
            .createNativeQuery(
                "SELECT COUNT(*) FROM subscription_plan WHERE subscription_plan_id = :id")
            .setParameter("id", planId)
            .getSingleResult()).longValue();
        assertThat(count).isEqualTo(1L);
    }

    @Test
    void findById_doesNotReturnSoftDeletedPlan() {
        SubscriptionPlan plan = SubscriptionPlan.builder()
            .name("SOFT_DELETED")
            .description("This will be soft deleted")
            .price(new BigDecimal("29.99"))
            .duration(30)
            .movies(new HashSet<>())
            .build();

        SubscriptionPlan saved = subscriptionPlanRepository.save(plan);
        Long planId = saved.getId();
        entityManager.flush();

        subscriptionPlanRepository.delete(saved);
        entityManager.flush();
        entityManager.clear();

        Optional<SubscriptionPlan> result = subscriptionPlanRepository.findById(planId);
        assertThat(result).isEmpty();
    }

    @Test
    void findAll_excludesSoftDeletedPlans() {
        SubscriptionPlan plan1 = SubscriptionPlan.builder()
            .name("ACTIVE_PLAN")
            .description("Active plan")
            .price(new BigDecimal("19.99"))
            .duration(30)
            .movies(new HashSet<>())
            .build();

        SubscriptionPlan plan2 = SubscriptionPlan.builder()
            .name("DELETED_PLAN")
            .description("Will be deleted")
            .price(new BigDecimal("29.99"))
            .duration(30)
            .movies(new HashSet<>())
            .build();

        subscriptionPlanRepository.saveAll(List.of(plan1, plan2));
        entityManager.flush();

        subscriptionPlanRepository.delete(plan2);
        entityManager.flush();
        entityManager.clear();

        List<SubscriptionPlan> allPlans = subscriptionPlanRepository.findAll();
        assertThat(allPlans)
            .extracting(SubscriptionPlan::getName)
            .contains("ACTIVE_PLAN")
            .doesNotContain("DELETED_PLAN");
    }

    // Optimistic locking

    @Test
    void updatePlan_incrementsVersionNumber() {
        SubscriptionPlan plan = SubscriptionPlan.builder()
            .name("VERSIONED_PLAN")
            .description("Plan with versioning")
            .price(new BigDecimal("49.99"))
            .duration(30)
            .movies(new HashSet<>())
            .build();

        SubscriptionPlan saved = subscriptionPlanRepository.save(plan);
        entityManager.flush();
        entityManager.clear();

        Long initialVersion = saved.getVersion();
        assertThat(initialVersion).isNotNull();

        SubscriptionPlan toUpdate = subscriptionPlanRepository.findById(saved.getId())
            .orElseThrow();
        toUpdate.setPrice(new BigDecimal("59.99"));
        subscriptionPlanRepository.save(toUpdate);
        entityManager.flush();
        entityManager.clear();

        SubscriptionPlan updated = subscriptionPlanRepository.findById(saved.getId()).orElseThrow();
        assertThat(updated.getVersion()).isGreaterThan(initialVersion);
        assertThat(updated.getPrice()).isEqualByComparingTo("59.99");
    }

    @Test
    void concurrentUpdate_withOptimisticLocking_throwsOptimisticLockException() {
        SubscriptionPlan plan = SubscriptionPlan.builder()
            .name("CONCURRENT_PLAN")
            .description("Plan for concurrent test")
            .price(new BigDecimal("39.99"))
            .duration(30)
            .movies(new HashSet<>())
            .build();

        SubscriptionPlan saved = subscriptionPlanRepository.save(plan);
        entityManager.flush();
        entityManager.clear();

        SubscriptionPlan plan1 = subscriptionPlanRepository.findById(saved.getId()).orElseThrow();
        entityManager.detach(plan1);

        SubscriptionPlan plan2 = subscriptionPlanRepository.findById(saved.getId()).orElseThrow();
        entityManager.detach(plan2);

        plan1.setPrice(new BigDecimal("44.99"));
        subscriptionPlanRepository.save(plan1);
        entityManager.flush();

        plan2.setPrice(new BigDecimal("49.99"));

        assertThatThrownBy(() -> {
            subscriptionPlanRepository.save(plan2);
            entityManager.flush();
        }).isInstanceOf(ObjectOptimisticLockingFailureException.class);
    }

    @Test
    void saveWithStaleVersion_throwsOptimisticLockException() {
        SubscriptionPlan plan = SubscriptionPlan.builder()
            .name("STALE_VERSION")
            .description("Plan with stale version")
            .price(new BigDecimal("29.99"))
            .duration(30)
            .movies(new HashSet<>())
            .build();

        SubscriptionPlan saved = subscriptionPlanRepository.save(plan);
        Long planId = saved.getId();
        entityManager.flush();
        entityManager.clear();

        SubscriptionPlan current = subscriptionPlanRepository.findById(planId).orElseThrow();
        current.setDescription("Updated description");
        subscriptionPlanRepository.save(current);
        entityManager.flush();
        entityManager.clear();


        saved.setPrice(new BigDecimal("39.99"));

        assertThatThrownBy(() -> {
            subscriptionPlanRepository.save(saved);
            entityManager.flush();
        }).isInstanceOf(ObjectOptimisticLockingFailureException.class);
    }

    // Pessimistic locking

    @Test
    void findByIdWithLock_appliesPessimisticWriteLock() {
        SubscriptionPlan plan = SubscriptionPlan.builder()
            .name("LOCKED_PLAN")
            .description("Plan for locking test")
            .price(new BigDecimal("49.99"))
            .duration(30)
            .movies(new HashSet<>())
            .build();

        SubscriptionPlan saved = subscriptionPlanRepository.save(plan);
        entityManager.flush();
        entityManager.clear();

        Optional<SubscriptionPlan> result = subscriptionPlanRepository.findByIdWithLock(
            saved.getId());

        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(saved.getId());
    }

    @Test
    void concurrentUpdateWithPessimisticLock_preventsConflicts() throws InterruptedException {
        TransactionTemplate setupTx = new TransactionTemplate(transactionManager);
        setupTx.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);

        Long planId = setupTx.execute(status -> {
            SubscriptionPlan plan = SubscriptionPlan.builder()
                .name("CONCURRENT_LOCK")
                .description("Plan for concurrent locking")
                .price(new BigDecimal("99.99"))
                .duration(30)
                .movies(new HashSet<>())
                .build();
            return subscriptionPlanRepository.save(plan).getId();
        });

        int threadCount = 3;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch doneLatch = new CountDownLatch(threadCount);
        AtomicInteger successCount = new AtomicInteger(0);

        TransactionTemplate txTemplate = new TransactionTemplate(transactionManager);
        txTemplate.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);

        for (int i = 0; i < threadCount; i++) {
            final int updateValue = i;
            executor.submit(() -> {
                try {
                    startLatch.await();
                    txTemplate.execute(status -> {
                        Optional<SubscriptionPlan> locked = subscriptionPlanRepository.findByIdWithLock(
                            planId);
                        if (locked.isPresent()) {
                            locked.get().setPrice(
                                new BigDecimal("100.00").add(new BigDecimal(updateValue)));
                            subscriptionPlanRepository.save(locked.get());
                            successCount.incrementAndGet();
                        }
                        return null;
                    });
                } catch (Exception ignored) {
                    // Lock timeout or other issue
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

        SubscriptionPlan result = readTx.execute(status ->
            subscriptionPlanRepository.findById(planId).orElseThrow()
        );

        assertThat(result.getPrice()).isGreaterThanOrEqualTo(new BigDecimal("100.00"));
        assertThat(successCount.get()).isGreaterThan(0);

        TransactionTemplate cleanupTx = new TransactionTemplate(transactionManager);
        cleanupTx.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
        cleanupTx.execute(status -> {
            subscriptionPlanRepository.deleteById(planId);
            return null;
        });
    }

    // ManyToMany relationship with movies

    @Test
    void savePlan_withMovies_persistsRelationship() {
        Set<Movie> movies = new HashSet<>();
        movies.add(movie1);
        movies.add(movie2);

        SubscriptionPlan plan = SubscriptionPlan.builder()
            .name("CINEMA_PLAN")
            .description("Plan with movie access")
            .price(new BigDecimal("69.99"))
            .duration(30)
            .movies(movies)
            .build();

        SubscriptionPlan saved = subscriptionPlanRepository.save(plan);
        entityManager.flush();
        entityManager.clear();

        SubscriptionPlan found = subscriptionPlanRepository.findById(saved.getId()).orElseThrow();
        assertThat(found.getMovies()).hasSize(2);
        assertThat(found.getMovies())
            .extracting(Movie::getTitle)
            .containsExactlyInAnyOrder("Inception", "Interstellar");
    }

    @Test
    void addMoviesToPlan_updatesJoinTable() {
        SubscriptionPlan plan = SubscriptionPlan.builder()
            .name("EXPANDABLE_PLAN")
            .description("Plan to expand")
            .price(new BigDecimal("49.99"))
            .duration(30)
            .movies(new HashSet<>())
            .build();

        SubscriptionPlan saved = subscriptionPlanRepository.save(plan);
        entityManager.flush();
        entityManager.clear();

        SubscriptionPlan toUpdate = subscriptionPlanRepository.findById(saved.getId())
            .orElseThrow();
        toUpdate.getMovies().add(movie1);
        toUpdate.getMovies().add(movie3);
        subscriptionPlanRepository.save(toUpdate);
        entityManager.flush();
        entityManager.clear();

        SubscriptionPlan updated = subscriptionPlanRepository.findById(saved.getId()).orElseThrow();
        assertThat(updated.getMovies()).hasSize(2);
        assertThat(updated.getMovies())
            .extracting(Movie::getTitle)
            .containsExactlyInAnyOrder("Inception", "The Dark Knight");
    }

    @Test
    void removeMoviesFromPlan_clearsJoinTable() {
        Set<Movie> movies = new HashSet<>();
        movies.add(movie1);
        movies.add(movie2);
        movies.add(movie3);

        SubscriptionPlan plan = SubscriptionPlan.builder()
            .name("FULL_PLAN")
            .description("Plan with all movies")
            .price(new BigDecimal("79.99"))
            .duration(30)
            .movies(movies)
            .build();

        SubscriptionPlan saved = subscriptionPlanRepository.save(plan);
        entityManager.flush();
        entityManager.clear();

        SubscriptionPlan toUpdate = subscriptionPlanRepository.findById(saved.getId())
            .orElseThrow();
        toUpdate.getMovies().clear();
        subscriptionPlanRepository.save(toUpdate);
        entityManager.flush();
        entityManager.clear();

        SubscriptionPlan updated = subscriptionPlanRepository.findById(saved.getId()).orElseThrow();
        assertThat(updated.getMovies()).isEmpty();
    }

    @Test
    void findWithMoviesById_eagerlyLoadsMovies() {
        Set<Movie> movies = new HashSet<>();
        movies.add(movie1);
        movies.add(movie2);

        SubscriptionPlan plan = SubscriptionPlan.builder()
            .name("EAGER_PLAN")
            .description("Plan for eager loading test")
            .price(new BigDecimal("89.99"))
            .duration(30)
            .movies(movies)
            .build();

        SubscriptionPlan saved = subscriptionPlanRepository.save(plan);
        entityManager.flush();
        entityManager.clear();

        Optional<SubscriptionPlan> result = subscriptionPlanRepository.findWithMoviesById(
            saved.getId());

        assertThat(result).isPresent();
        SubscriptionPlan found = result.get();
        assertThat(found.getMovies()).isNotNull();
        assertThat(found.getMovies()).hasSize(2);

        found.getMovies().forEach(movie -> assertThat(movie.getTitle()).isNotNull());
    }

    // Data integrity and validation

    @Test
    void savePlan_withDuplicateName_throwsException() {
        SubscriptionPlan plan1 = SubscriptionPlan.builder()
            .name("UNIQUE_NAME")
            .description("First plan")
            .price(new BigDecimal("29.99"))
            .duration(30)
            .movies(new HashSet<>())
            .build();

        subscriptionPlanRepository.save(plan1);
        entityManager.flush();

        SubscriptionPlan plan2 = SubscriptionPlan.builder()
            .name("UNIQUE_NAME")
            .description("Second plan")
            .price(new BigDecimal("39.99"))
            .duration(30)
            .movies(new HashSet<>())
            .build();

        assertThatThrownBy(() -> {
            subscriptionPlanRepository.save(plan2);
            entityManager.flush();
        }).isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    void savePlan_withNegativePrice_failsValidation() {
        SubscriptionPlan plan = SubscriptionPlan.builder()
            .name("NEGATIVE_PRICE")
            .description("Invalid plan")
            .price(new BigDecimal("-10.00"))
            .duration(30)
            .movies(new HashSet<>())
            .build();

        assertThatThrownBy(() -> {
            subscriptionPlanRepository.save(plan);
            entityManager.flush();
        }).isInstanceOf(ConstraintViolationException.class);
    }

    @Test
    void savePlan_withInvalidDuration_failsValidation() {
        SubscriptionPlan plan = SubscriptionPlan.builder()
            .name("INVALID_DURATION")
            .description("Plan with invalid duration")
            .price(new BigDecimal("29.99"))
            .duration(0)
            .movies(new HashSet<>())
            .build();

        assertThatThrownBy(() -> {
            subscriptionPlanRepository.save(plan);
            entityManager.flush();
        }).isInstanceOf(ConstraintViolationException.class);
    }

    @Test
    void savePlan_withNullRequiredFields_failsConstraint() {
        SubscriptionPlan planNullName = SubscriptionPlan.builder()
            .name(null)
            .description("Description")
            .price(new BigDecimal("29.99"))
            .duration(30)
            .movies(new HashSet<>())
            .build();

        assertThatThrownBy(() -> {
            subscriptionPlanRepository.save(planNullName);
            entityManager.flush();
        }).isInstanceOf(ConstraintViolationException.class);

        SubscriptionPlan planNullPrice = SubscriptionPlan.builder()
            .name("NULL_PRICE")
            .description("Description")
            .price(null)
            .duration(30)
            .movies(new HashSet<>())
            .build();

        assertThatThrownBy(() -> {
            subscriptionPlanRepository.save(planNullPrice);
            entityManager.flush();
        }).isInstanceOf(ConstraintViolationException.class);
    }
}

