package dev.studentpp1.streamingservice.subscription.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import dev.studentpp1.streamingservice.movies.entity.Director;
import dev.studentpp1.streamingservice.movies.entity.Movie;
import dev.studentpp1.streamingservice.movies.mapper.MovieMapperImpl;
import dev.studentpp1.streamingservice.subscription.dto.request.CreateSubscriptionPlanRequest;
import dev.studentpp1.streamingservice.subscription.dto.response.SubscriptionPlanDetailsDto;
import dev.studentpp1.streamingservice.subscription.dto.response.SubscriptionPlanSummaryDto;
import dev.studentpp1.streamingservice.subscription.entity.SubscriptionPlan;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {SubscriptionPlanMapperImpl.class, MovieMapperImpl.class})
class SubscriptionPlanMapperTest {

    @Autowired
    private SubscriptionPlanMapper subscriptionPlanMapper;

    @Test
    void toSummaryDto_mapsAllFieldsCorrectly() {
        SubscriptionPlan plan = SubscriptionPlan.builder()
            .id(1L)
            .name("GOLD")
            .description("Gold subscription plan")
            .price(new BigDecimal("49.99"))
            .duration(60)
            .movies(new HashSet<>())
            .version(1L)
            .build();

        SubscriptionPlanSummaryDto dto = subscriptionPlanMapper.toSummaryDto(plan);

        assertThat(dto).isNotNull();
        assertThat(dto.id()).isEqualTo(1L);
        assertThat(dto.name()).isEqualTo("GOLD");
        assertThat(dto.description()).isEqualTo("Gold subscription plan");
        assertThat(dto.price()).isEqualByComparingTo("49.99");
        assertThat(dto.duration()).isEqualTo(60);
    }

    @Test
    void toSummaryDto_withNullFields_handlesGracefully() {
        SubscriptionPlan plan = SubscriptionPlan.builder()
            .id(null)
            .name("TEST")
            .description("Test plan")
            .price(new BigDecimal("10.00"))
            .duration(30)
            .build();

        SubscriptionPlanSummaryDto dto = subscriptionPlanMapper.toSummaryDto(plan);

        assertThat(dto).isNotNull();
        assertThat(dto.id()).isNull();
        assertThat(dto.name()).isEqualTo("TEST");
    }

    @Test
    void toSummaryDto_preservesBigDecimalPrecision() {
        SubscriptionPlan plan = SubscriptionPlan.builder()
            .id(1L)
            .name("PRECISION_TEST")
            .description("Test precision")
            .price(new BigDecimal("99.99"))
            .duration(30)
            .build();

        SubscriptionPlanSummaryDto dto = subscriptionPlanMapper.toSummaryDto(plan);

        assertThat(dto.price()).isEqualByComparingTo("99.99");
        assertThat(dto.price().scale()).isEqualTo(2);
    }

    @Test
    void toDetailsDto_mapsAllFieldsIncludingMovies() {
        Director director = new Director();
        director.setId(1L);
        director.setName("Christopher");
        director.setSurname("Nolan");

        Movie movie1 = new Movie();
        movie1.setId(1L);
        movie1.setTitle("Inception");
        movie1.setDescription("Mind-bending thriller");
        movie1.setYear(2010);
        movie1.setRating(8.8);
        movie1.setDirector(director);

        Movie movie2 = new Movie();
        movie2.setId(2L);
        movie2.setTitle("Interstellar");
        movie2.setDescription("Space exploration");
        movie2.setYear(2014);
        movie2.setRating(8.6);
        movie2.setDirector(director);

        Set<Movie> movies = new HashSet<>();
        movies.add(movie1);
        movies.add(movie2);

        SubscriptionPlan plan = SubscriptionPlan.builder()
            .id(1L)
            .name("CINEMA_PLAN")
            .description("Plan with movies")
            .price(new BigDecimal("79.99"))
            .duration(90)
            .movies(movies)
            .build();

        SubscriptionPlanDetailsDto dto = subscriptionPlanMapper.toDetailsDto(plan);

        assertThat(dto).isNotNull();
        assertThat(dto.id()).isEqualTo(1L);
        assertThat(dto.name()).isEqualTo("CINEMA_PLAN");
        assertThat(dto.description()).isEqualTo("Plan with movies");
        assertThat(dto.price()).isEqualByComparingTo("79.99");
        assertThat(dto.duration()).isEqualTo(90);
        assertThat(dto.movies()).hasSize(2);
        assertThat(dto.movies())
            .extracting("title")
            .containsExactlyInAnyOrder("Inception", "Interstellar");
    }

    @Test
    void toDetailsDto_withEmptyMovies_returnsEmptySet() {
        SubscriptionPlan plan = SubscriptionPlan.builder()
            .id(1L)
            .name("BASIC")
            .description("Basic plan")
            .price(new BigDecimal("19.99"))
            .duration(30)
            .movies(new HashSet<>())
            .build();

        SubscriptionPlanDetailsDto dto = subscriptionPlanMapper.toDetailsDto(plan);

        assertThat(dto.movies()).isNotNull();
        assertThat(dto.movies()).isEmpty();
    }

    @Test
    void toDetailsDto_withNullMovies_handlesGracefully() {
        SubscriptionPlan plan = SubscriptionPlan.builder()
            .id(1L)
            .name("NULL_MOVIES")
            .description("Plan with null movies")
            .price(new BigDecimal("29.99"))
            .duration(30)
            .movies(null)
            .build();

        SubscriptionPlanDetailsDto dto = subscriptionPlanMapper.toDetailsDto(plan);

        assertThat(dto).isNotNull();
        assertThat(dto.name()).isEqualTo("NULL_MOVIES");
        assertThat(dto.movies()).isNull();
    }

    @Test
    void toDetailsDto_usesMovieMapper() {
        Director director = new Director();
        director.setId(5L);
        director.setName("Quentin");
        director.setSurname("Tarantino");

        Movie movie = new Movie();
        movie.setId(10L);
        movie.setTitle("Pulp Fiction");
        movie.setDescription("Crime drama");
        movie.setYear(1994);
        movie.setRating(8.9);
        movie.setDirector(director);

        Set<Movie> movies = new HashSet<>();
        movies.add(movie);

        SubscriptionPlan plan = SubscriptionPlan.builder()
            .id(1L)
            .name("CLASSIC")
            .description("Classic movies")
            .price(new BigDecimal("39.99"))
            .duration(60)
            .movies(movies)
            .build();

        SubscriptionPlanDetailsDto dto = subscriptionPlanMapper.toDetailsDto(plan);

        assertThat(dto.movies()).hasSize(1);
        assertThat(dto.movies().iterator().next().id()).isEqualTo(10L);
        assertThat(dto.movies().iterator().next().title()).isEqualTo("Pulp Fiction");
        assertThat(dto.movies().iterator().next().directorId()).isEqualTo(5L);
        assertThat(dto.movies().iterator().next().directorName()).isEqualTo("Quentin");
        assertThat(dto.movies().iterator().next().directorSurname()).isEqualTo("Tarantino");
    }

    @Test
    void toEntity_mapsRequestToNewEntity() {
        CreateSubscriptionPlanRequest request = new CreateSubscriptionPlanRequest(
            "PLATINUM",
            "Platinum plan with all features",
            new BigDecimal("149.99"),
            120,
            null
        );

        SubscriptionPlan entity = subscriptionPlanMapper.toEntity(request);

        assertThat(entity).isNotNull();
        assertThat(entity.getName()).isEqualTo("PLATINUM");
        assertThat(entity.getDescription()).isEqualTo("Platinum plan with all features");
        assertThat(entity.getPrice()).isEqualByComparingTo("149.99");
        assertThat(entity.getDuration()).isEqualTo(120);
    }

    @Test
    void toEntity_ignoresIdMoviesAndVersion() {
        CreateSubscriptionPlanRequest request = new CreateSubscriptionPlanRequest(
            "NEW_PLAN",
            "New plan description",
            new BigDecimal("29.99"),
            30,
            null
        );

        SubscriptionPlan entity = subscriptionPlanMapper.toEntity(request);

        assertThat(entity.getId()).isNull();
        assertThat(entity.getMovies()).isEmpty();
        assertThat(entity.getVersion()).isNull();
    }

    @Test
    void toEntity_withAllFields_createsCompleteEntity() {
        CreateSubscriptionPlanRequest request = new CreateSubscriptionPlanRequest(
            "COMPLETE",
            "Complete plan with all fields",
            new BigDecimal("99.99"),
            90,
            null
        );

        SubscriptionPlan entity = subscriptionPlanMapper.toEntity(request);

        assertThat(entity.getName()).isEqualTo("COMPLETE");
        assertThat(entity.getDescription()).isEqualTo("Complete plan with all fields");
        assertThat(entity.getPrice()).isEqualByComparingTo("99.99");
        assertThat(entity.getDuration()).isEqualTo(90);
    }

    @Test
    void updateEntityFromDto_updatesExistingEntity() {
        SubscriptionPlan existingPlan = SubscriptionPlan.builder()
            .id(42L)
            .name("OLD_NAME")
            .description("Old description")
            .price(new BigDecimal("19.99"))
            .duration(30)
            .movies(new HashSet<>())
            .version(5L)
            .build();

        CreateSubscriptionPlanRequest request = new CreateSubscriptionPlanRequest(
            "NEW_NAME",
            "New description",
            new BigDecimal("39.99"),
            60,
            null
        );

        subscriptionPlanMapper.updateEntityFromDto(request, existingPlan);

        assertThat(existingPlan.getName()).isEqualTo("NEW_NAME");
        assertThat(existingPlan.getDescription()).isEqualTo("New description");
        assertThat(existingPlan.getPrice()).isEqualByComparingTo("39.99");
        assertThat(existingPlan.getDuration()).isEqualTo(60);
    }

    @Test
    void updateEntityFromDto_doesNotChangeIdMoviesVersion() {
        Movie existingMovie = new Movie();
        existingMovie.setId(1L);
        existingMovie.setTitle("Existing Movie");

        Set<Movie> existingMovies = new HashSet<>();
        existingMovies.add(existingMovie);

        SubscriptionPlan existingPlan = SubscriptionPlan.builder()
            .id(100L)
            .name("OLD_PLAN")
            .description("Old description")
            .price(new BigDecimal("29.99"))
            .duration(30)
            .movies(existingMovies)
            .version(10L)
            .build();

        CreateSubscriptionPlanRequest request = new CreateSubscriptionPlanRequest(
            "UPDATED_PLAN",
            "Updated description",
            new BigDecimal("49.99"),
            60,
            null
        );

        subscriptionPlanMapper.updateEntityFromDto(request, existingPlan);

        assertThat(existingPlan.getId()).isEqualTo(100L);
        assertThat(existingPlan.getVersion()).isEqualTo(10L);
        assertThat(existingPlan.getMovies()).hasSize(1);
        assertThat(existingPlan.getMovies().iterator().next().getTitle()).isEqualTo(
            "Existing Movie");
    }

    @Test
    void updateEntityFromDto_updatesOnlyMappedFields() {
        SubscriptionPlan existingPlan = SubscriptionPlan.builder()
            .id(1L)
            .name("ORIGINAL")
            .description("Original description")
            .price(new BigDecimal("10.00"))
            .duration(7)
            .movies(new HashSet<>())
            .version(1L)
            .build();

        Long originalId = existingPlan.getId();
        Long originalVersion = existingPlan.getVersion();
        Set<Movie> originalMovies = existingPlan.getMovies();

        CreateSubscriptionPlanRequest request = new CreateSubscriptionPlanRequest(
            "MODIFIED",
            "Modified description",
            new BigDecimal("20.00"),
            14,
            null
        );

        subscriptionPlanMapper.updateEntityFromDto(request, existingPlan);

        assertThat(existingPlan.getName()).isEqualTo("MODIFIED");
        assertThat(existingPlan.getDescription()).isEqualTo("Modified description");
        assertThat(existingPlan.getPrice()).isEqualByComparingTo("20.00");
        assertThat(existingPlan.getDuration()).isEqualTo(14);

        assertThat(existingPlan.getId()).isEqualTo(originalId);
        assertThat(existingPlan.getVersion()).isEqualTo(originalVersion);
        assertThat(existingPlan.getMovies()).isSameAs(originalMovies);
    }
}
