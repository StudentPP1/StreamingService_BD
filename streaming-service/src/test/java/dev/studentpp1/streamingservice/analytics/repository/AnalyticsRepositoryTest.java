package dev.studentpp1.streamingservice.analytics.repository;

import static org.assertj.core.api.Assertions.assertThat;

import dev.studentpp1.streamingservice.AbstractPostgresContainerTest;
import dev.studentpp1.streamingservice.analytics.dto.DirectorRevenueStats;
import dev.studentpp1.streamingservice.analytics.dto.MonthlyPlanStatisticProjection;
import dev.studentpp1.streamingservice.auth.persistence.Role;
import dev.studentpp1.streamingservice.movies.entity.Director;
import dev.studentpp1.streamingservice.movies.entity.Movie;
import dev.studentpp1.streamingservice.movies.repository.DirectorRepository;
import dev.studentpp1.streamingservice.movies.repository.MovieRepository;
import dev.studentpp1.streamingservice.movies.repository.PerformanceRepository;
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
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;

@DataJpaTest
@DirtiesContext(classMode = ClassMode.BEFORE_CLASS)
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

    @Autowired
    private DirectorRepository directorRepository;

    @Autowired
    private MovieRepository movieRepository;

    @Autowired
    private PerformanceRepository performanceRepository;

    @Autowired
    private TestEntityManager entityManager;

    private AppUser user1;
    private AppUser user2;
    private SubscriptionPlan basic, standard, premium;
    private Director nolan;

    @BeforeEach
    void setUp() {
        paymentRepository.deleteAll();
        userSubscriptionRepository.deleteAll();
        performanceRepository.deleteAll();

        List<SubscriptionPlan> plans = subscriptionPlanRepository.findAll();
        for (SubscriptionPlan plan : plans) {
            plan.getMovies().clear();
            subscriptionPlanRepository.save(plan);
        }
        entityManager.flush();

        movieRepository.deleteAll();
        directorRepository.deleteAll();
        userRepository.deleteAll();

        // ---------- USERS ----------
        user1 = AppUser.builder()
            .name("John")
            .surname("Doe")
            .email("user1@example.com")
            .password("password1")
            .birthday(LocalDate.of(1990, 1, 1))
            .role(Role.ROLE_USER)
            .build();
        user1 = userRepository.save(user1);

        user2 = AppUser.builder()
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
        basic = subscriptionPlanRepository.findByName("BASIC")
            .orElseThrow(() -> new IllegalStateException("BASIC plan not found"));

        standard = subscriptionPlanRepository.findByName("STANDARD")
            .orElseThrow(() -> new IllegalStateException("STANDARD plan not found"));

        premium = subscriptionPlanRepository.findByName("PREMIUM")
            .orElseThrow(() -> new IllegalStateException("PREMIUM plan not found"));

        // ---------- DIRECTORS ----------
        nolan = new Director();
        nolan.setName("Christopher");
        nolan.setSurname("Nolan");
        nolan.setBiography("Famous director");
        nolan = directorRepository.save(nolan);

        Director tarantino = new Director();
        tarantino.setName("Quentin");
        tarantino.setSurname("Tarantino");
        tarantino.setBiography("Acclaimed filmmaker");
        tarantino = directorRepository.save(tarantino);

        Director spielberg = new Director();
        spielberg.setName("Steven");
        spielberg.setSurname("Spielberg");
        spielberg.setBiography("Legendary director");
        spielberg = directorRepository.save(spielberg);

        Director kubrick = new Director();
        kubrick.setName("Stanley");
        kubrick.setSurname("Kubrick");
        kubrick.setBiography("Visionary director");
        kubrick = directorRepository.save(kubrick);

        // ---------- MOVIES ----------
        Movie inception = new Movie();
        inception.setTitle("Inception");
        inception.setDescription("Mind-bending thriller");
        inception.setYear(2010);
        inception.setRating(8.8);
        inception.setDirector(nolan);
        inception = movieRepository.save(inception);

        Movie pulpFiction = new Movie();
        pulpFiction.setTitle("Pulp Fiction");
        pulpFiction.setDescription("Crime drama");
        pulpFiction.setYear(1994);
        pulpFiction.setRating(8.9);
        pulpFiction.setDirector(tarantino);
        pulpFiction = movieRepository.save(pulpFiction);

        Movie jaws = new Movie();
        jaws.setTitle("Jaws");
        jaws.setDescription("Shark thriller");
        jaws.setYear(1975);
        jaws.setRating(8.1);
        jaws.setDirector(spielberg);
        jaws = movieRepository.save(jaws);

        Movie shining = new Movie();
        shining.setTitle("The Shining");
        shining.setDescription("Horror classic");
        shining.setYear(1980);
        shining.setRating(8.4);
        shining.setDirector(kubrick);
        shining = movieRepository.save(shining);

        // ---------- ASSOCIATE MOVIES WITH PLANS ----------
        addMovieToPlan(inception, premium);
        addMovieToPlan(pulpFiction, standard);
        addMovieToPlan(jaws, premium);
        addMovieToPlan(shining, basic);

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

        entityManager.flush();
    }

    private void addMovieToPlan(Movie movie, SubscriptionPlan plan) {
        Set<Movie> movies = plan.getMovies();
        if (movies == null) {
            movies = new HashSet<>();
        }
        movies.add(movie);
        plan.setMovies(movies);
        subscriptionPlanRepository.save(plan);
        entityManager.flush();
    }

    @Test
    void findMonthlyPlanStatistics_returnsCorrectData() {
        List<MonthlyPlanStatisticProjection> stats = analyticsRepository.findMonthlyPlanStatistics();
        assertThat(stats).hasSize(5);
    }

    @Test
    void findTopDirectorsAggregated_returnsTop10DirectorsByRevenue() {
        for (int i = 1; i <= 12; i++) {
            Director director = new Director();
            director.setName("Director" + i);
            director.setSurname("Test");
            director = directorRepository.save(director);

            Movie movie = new Movie();
            movie.setTitle("Movie" + i);
            movie.setDescription("Test movie");
            movie.setYear(2020);
            movie.setRating(7.0);
            movie.setDirector(director);
            movie = movieRepository.save(movie);

            addMovieToPlan(movie, basic);

            AppUser user = AppUser.builder()
                .name("User" + i)
                .surname("Test")
                .email("user" + i + "@test.com")
                .password("password")
                .birthday(LocalDate.of(1990, 1, 1))
                .role(Role.ROLE_USER)
                .build();
            user = userRepository.save(user);

            UserSubscription subscription = UserSubscription.builder()
                .user(user)
                .plan(basic)
                .startTime(LocalDateTime.of(2025, 1, 1, 0, 0))
                .endTime(LocalDateTime.of(2025, 12, 31, 0, 0))
                .status(SubscriptionStatus.ACTIVE)
                .build();
            subscription = userSubscriptionRepository.save(subscription);

            paymentRepository.save(Payment.builder()
                .userSubscription(subscription)
                .status(PaymentStatus.COMPLETED)
                .paidAt(LocalDateTime.of(2025, 6, 1, 10, 0))
                .amount(BigDecimal.valueOf(13 - i))
                .build());
        }
        entityManager.flush();

        LocalDateTime startDate = LocalDateTime.of(2025, 1, 1, 0, 0);
        LocalDateTime endDate = LocalDateTime.of(2025, 12, 31, 23, 59);

        List<DirectorRevenueStats> results = analyticsRepository.findTopDirectorsAggregated(
            startDate, endDate);

        assertThat(results).hasSizeLessThanOrEqualTo(10);

        for (int i = 0; i < results.size() - 1; i++) {
            assertThat(results.get(i).getTotalRevenue())
                .isGreaterThanOrEqualTo(results.get(i + 1).getTotalRevenue());
        }
    }

    @Test
    void findTopDirectorsAggregated_calculatesRevenueCorrectly() {
        Movie interstellar = new Movie();
        interstellar.setTitle("Interstellar");
        interstellar.setDescription("Space epic");
        interstellar.setYear(2014);
        interstellar.setRating(8.6);
        interstellar.setDirector(nolan);
        interstellar = movieRepository.save(interstellar);
        addMovieToPlan(interstellar, standard);

        UserSubscription user2StandardSub = userSubscriptionRepository.findByUser(user2).get(0);
        paymentRepository.save(Payment.builder()
            .userSubscription(user2StandardSub)
            .status(PaymentStatus.COMPLETED)
            .paidAt(LocalDateTime.of(2025, 6, 15, 10, 0))
            .amount(BigDecimal.valueOf(75))
            .build());

        entityManager.flush();

        LocalDateTime startDate = LocalDateTime.of(2025, 1, 1, 0, 0);
        LocalDateTime endDate = LocalDateTime.of(2025, 12, 31, 23, 59);

        List<DirectorRevenueStats> results = analyticsRepository.findTopDirectorsAggregated(
            startDate, endDate);

        DirectorRevenueStats nolanStats = results.stream()
            .filter(s -> s.getDirectorName().equals("Christopher Nolan"))
            .findFirst()
            .orElse(null);

        assertThat(nolanStats).isNotNull();
        BigDecimal expectedRevenue = BigDecimal.valueOf(200 + 200 + 150 + 150 + 75);
        assertThat(nolanStats.getTotalRevenue()).isEqualByComparingTo(expectedRevenue);
    }

    @Test
    void findTopDirectorsAggregated_withDateFilter_returnsOnlyPaymentsInRange() {
        LocalDateTime startDate = LocalDateTime.of(2025, 12, 1, 0, 0);
        LocalDateTime endDate = LocalDateTime.of(2025, 12, 31, 23, 59);

        List<DirectorRevenueStats> results = analyticsRepository.findTopDirectorsAggregated(
            startDate, endDate);

        assertThat(results).hasSizeGreaterThanOrEqualTo(3);

        results.stream()
            .filter(s -> s.getDirectorName().equals("Stanley Kubrick"))
            .findFirst().ifPresent(
                kubrickStats -> assertThat(kubrickStats.getTotalRevenue())
                    .isEqualByComparingTo(BigDecimal.valueOf(100))
            );
    }

    @Test
    void findTopDirectorsAggregated_aggregatesRevenueBySQLDateFilter() {
        LocalDateTime startDate = LocalDateTime.of(2025, 11, 1, 0, 0);
        LocalDateTime endDate = LocalDateTime.of(2025, 11, 30, 23, 59);

        List<DirectorRevenueStats> results = analyticsRepository.findTopDirectorsAggregated(
            startDate, endDate);

        assertThat(results).isNotEmpty();

        for (DirectorRevenueStats stats : results) {
            assertThat(stats.getDirectorName()).isNotNull();
            assertThat(stats.getTotalRevenue()).isGreaterThan(BigDecimal.ZERO);
        }
    }

    @Test
    void findTopDirectorsAggregated_rankingWithDenseRank_handlesMultipleDirectors() {
        LocalDateTime startDate = LocalDateTime.of(2025, 1, 1, 0, 0);
        LocalDateTime endDate = LocalDateTime.of(2025, 12, 31, 23, 59);

        List<DirectorRevenueStats> results = analyticsRepository.findTopDirectorsAggregated(
            startDate, endDate);

        assertThat(results).isNotEmpty();

        for (int i = 0; i < results.size(); i++) {
            Integer rank = results.get(i).getRevenueRank();
            assertThat(rank).isNotNull();
            assertThat(rank).isGreaterThan(0);

            if (i > 0) {
                assertThat(rank).isGreaterThanOrEqualTo(results.get(i - 1).getRevenueRank());
            }
        }
    }

    @Test
    void findTopDirectorsAggregated_includesPlanNamesAndBreakdown() {
        LocalDateTime startDate = LocalDateTime.of(2025, 1, 1, 0, 0);
        LocalDateTime endDate = LocalDateTime.of(2025, 12, 31, 23, 59);

        List<DirectorRevenueStats> results = analyticsRepository.findTopDirectorsAggregated(
            startDate, endDate);

        DirectorRevenueStats nolanStats = results.stream()
            .filter(s -> s.getDirectorName().equals("Christopher Nolan"))
            .findFirst()
            .orElse(null);

        assertThat(nolanStats).isNotNull();

        String planNames = nolanStats.getPlanNames();
        assertThat(planNames).isNotNull();
        assertThat(planNames).contains("PREMIUM");

        String breakdown = nolanStats.getRevenueBreakdownJson();
        assertThat(breakdown).isNotNull();
        assertThat(breakdown).contains("PREMIUM");
        assertThat(breakdown).contains("\"");
    }


    @Test
    void findTopDirectorsAggregated_withNoPayments_returnsEmptyList() {
        LocalDateTime startDate = LocalDateTime.of(2024, 1, 1, 0, 0);
        LocalDateTime endDate = LocalDateTime.of(2024, 12, 31, 23, 59);

        List<DirectorRevenueStats> results = analyticsRepository.findTopDirectorsAggregated(
            startDate, endDate);

        assertThat(results).isEmpty();
    }

    @Test
    void findTopDirectorsAggregated_withOnlyOneDirector_returnsSingleResult() {
        paymentRepository.deleteAll();
        userSubscriptionRepository.deleteAll();

        List<SubscriptionPlan> plans = subscriptionPlanRepository.findAll();
        for (SubscriptionPlan plan : plans) {
            plan.getMovies().clear();
            subscriptionPlanRepository.save(plan);
        }
        entityManager.flush();

        movieRepository.deleteAll();
        directorRepository.deleteAll();
        entityManager.flush();

        Director singleDirector = new Director();
        singleDirector.setName("Solo");
        singleDirector.setSurname("Director");
        singleDirector = directorRepository.save(singleDirector);

        Movie singleMovie = new Movie();
        singleMovie.setTitle("Single Movie");
        singleMovie.setDescription("Only movie");
        singleMovie.setYear(2020);
        singleMovie.setRating(8.0);
        singleMovie.setDirector(singleDirector);
        singleMovie = movieRepository.save(singleMovie);

        addMovieToPlan(singleMovie, premium);

        UserSubscription subscription = UserSubscription.builder()
            .user(user1)
            .plan(premium)
            .startTime(LocalDateTime.of(2025, 1, 1, 0, 0))
            .endTime(LocalDateTime.of(2025, 12, 31, 0, 0))
            .status(SubscriptionStatus.ACTIVE)
            .build();
        subscription = userSubscriptionRepository.save(subscription);

        paymentRepository.save(Payment.builder()
            .userSubscription(subscription)
            .status(PaymentStatus.COMPLETED)
            .paidAt(LocalDateTime.of(2025, 6, 15, 10, 0))
            .amount(BigDecimal.valueOf(100))
            .build());

        entityManager.flush();

        LocalDateTime startDate = LocalDateTime.of(2025, 1, 1, 0, 0);
        LocalDateTime endDate = LocalDateTime.of(2025, 12, 31, 23, 59);

        List<DirectorRevenueStats> results = analyticsRepository.findTopDirectorsAggregated(
            startDate, endDate);

        assertThat(results).hasSize(1);
        assertThat(results.getFirst().getDirectorName()).isEqualTo("Solo Director");
        assertThat(results.getFirst().getRevenueRank()).isEqualTo(1);
    }

    @Test
    void findTopDirectorsAggregated_excludesFailedPayments() {
        LocalDateTime startDate = LocalDateTime.of(2025, 11, 1, 0, 0);
        LocalDateTime endDate = LocalDateTime.of(2025, 11, 30, 23, 59);

        List<DirectorRevenueStats> results = analyticsRepository.findTopDirectorsAggregated(
            startDate, endDate);

        results.stream()
            .filter(s -> s.getDirectorName().equals("Christopher Nolan"))
            .findFirst().ifPresent(
                nolanStats -> assertThat(nolanStats.getTotalRevenue())
                    .isEqualByComparingTo(BigDecimal.valueOf(200))
            );
    }

    @Test
    void findTopDirectorsAggregated_requiresMovieInSubscriptionPlan() {
        Director unmappedDirector = new Director();
        unmappedDirector.setName("Unmapped");
        unmappedDirector.setSurname("Director");
        unmappedDirector = directorRepository.save(unmappedDirector);

        Movie unmappedMovie = new Movie();
        unmappedMovie.setTitle("Unmapped Movie");
        unmappedMovie.setDescription("Not in any plan");
        unmappedMovie.setYear(2020);
        unmappedMovie.setRating(7.0);
        unmappedMovie.setDirector(unmappedDirector);
        movieRepository.save(unmappedMovie);

        entityManager.flush();

        LocalDateTime startDate = LocalDateTime.of(2025, 1, 1, 0, 0);
        LocalDateTime endDate = LocalDateTime.of(2025, 12, 31, 23, 59);

        List<DirectorRevenueStats> results = analyticsRepository.findTopDirectorsAggregated(
            startDate, endDate);

        boolean unmappedFound = results.stream()
            .anyMatch(s -> s.getDirectorName().equals("Unmapped Director"));

        assertThat(unmappedFound).isFalse();
    }

    @Test
    void findTopDirectorsAggregated_readsCommittedData() {
        LocalDateTime startDate = LocalDateTime.of(2025, 1, 1, 0, 0);
        LocalDateTime endDate = LocalDateTime.of(2025, 12, 31, 23, 59);

        List<DirectorRevenueStats> results1 = analyticsRepository.findTopDirectorsAggregated(
            startDate, endDate);
        entityManager.flush();
        entityManager.clear();
        List<DirectorRevenueStats> results2 = analyticsRepository.findTopDirectorsAggregated(
            startDate, endDate);

        assertThat(results1).hasSameSizeAs(results2);

        for (int i = 0; i < results1.size(); i++) {
            assertThat(results1.get(i).getDirectorName()).isEqualTo(
                results2.get(i).getDirectorName());
            assertThat(results1.get(i).getTotalRevenue()).isEqualByComparingTo(
                results2.get(i).getTotalRevenue());
            assertThat(results1.get(i).getRevenueRank()).isEqualTo(
                results2.get(i).getRevenueRank());
        }
    }
}
