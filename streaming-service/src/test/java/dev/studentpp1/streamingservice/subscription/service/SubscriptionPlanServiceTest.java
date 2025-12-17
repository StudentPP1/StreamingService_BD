package dev.studentpp1.streamingservice.subscription.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyList;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import dev.studentpp1.streamingservice.movies.entity.Movie;
import dev.studentpp1.streamingservice.movies.repository.MovieRepository;
import dev.studentpp1.streamingservice.subscription.dto.request.CreateSubscriptionPlanRequest;
import dev.studentpp1.streamingservice.subscription.entity.SubscriptionPlan;
import dev.studentpp1.streamingservice.subscription.exception.MoviesNotFoundException;
import dev.studentpp1.streamingservice.subscription.exception.MoviesNotInPlanException;
import dev.studentpp1.streamingservice.subscription.exception.SubscriptionPlanAlreadyExistsException;
import dev.studentpp1.streamingservice.subscription.mapper.SubscriptionPlanMapper;
import dev.studentpp1.streamingservice.subscription.repository.SubscriptionPlanRepository;
import dev.studentpp1.streamingservice.subscription.repository.UserSubscriptionRepository;
import dev.studentpp1.streamingservice.subscription.service.utils.SubscriptionPlanUtils;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
class SubscriptionPlanServiceTest {

    @Mock
    private SubscriptionPlanRepository subscriptionPlanRepository;

    @Mock
    private UserSubscriptionRepository userSubscriptionRepository;

    @Mock
    private MovieRepository movieRepository;

    @Mock
    private SubscriptionPlanMapper subscriptionPlanMapper;

    @Mock
    private SubscriptionPlanUtils subscriptionPlanUtils;

    @InjectMocks
    private SubscriptionPlanService subscriptionPlanService;

    private SubscriptionPlan createTestPlan() {
        return SubscriptionPlan.builder()
            .id(1L)
            .name("TEST_PLAN")
            .description("Test description")
            .price(new BigDecimal("29.99"))
            .duration(30)
            .movies(new HashSet<>())
            .build();
    }

    private SubscriptionPlan createTestPlanWithMovies(Set<Movie> movies) {
        return SubscriptionPlan.builder()
            .id(1L)
            .name("TEST_PLAN")
            .description("Test description")
            .price(new BigDecimal("29.99"))
            .duration(30)
            .movies(movies)
            .build();
    }

    private Movie createTestMovie(Long id, String title) {
        Movie movie = new Movie();
        movie.setId(id);
        movie.setTitle(title);
        movie.setDescription("Description for " + title);
        movie.setYear(2020);
        movie.setRating(8.5);
        return movie;
    }

    private CreateSubscriptionPlanRequest createTestRequest(List<Long> movieIds) {
        return new CreateSubscriptionPlanRequest(
            "NEW_PLAN",
            "New plan description",
            new BigDecimal("49.99"),
            60,
            movieIds
        );
    }

    @Test
    void getAllPlans_withoutSearch_returnsAllPlans() {
        Pageable pageable = PageRequest.of(0, 10);
        SubscriptionPlan plan1 = createTestPlan();
        SubscriptionPlan plan2 = createTestPlan();
        Page<SubscriptionPlan> expectedPage = new PageImpl<>(List.of(plan1, plan2));

        when(subscriptionPlanRepository.findAll(pageable)).thenReturn(expectedPage);

        Page<SubscriptionPlan> result = subscriptionPlanService.getAllPlans(null, pageable);

        assertThat(result).isEqualTo(expectedPage);
        assertThat(result.getContent()).hasSize(2);
        verify(subscriptionPlanRepository).findAll(pageable);
        verify(subscriptionPlanRepository, never()).findAllByNameContainingIgnoreCase(anyString(),
            any());
    }

    @Test
    void getAllPlans_withSearch_returnsFilteredPlans() {
        String search = "premium";
        Pageable pageable = PageRequest.of(0, 10);
        SubscriptionPlan premiumPlan = createTestPlan();
        Page<SubscriptionPlan> expectedPage = new PageImpl<>(List.of(premiumPlan));

        when(subscriptionPlanRepository.findAllByNameContainingIgnoreCase(search, pageable))
            .thenReturn(expectedPage);

        Page<SubscriptionPlan> result = subscriptionPlanService.getAllPlans(search, pageable);

        assertThat(result).isEqualTo(expectedPage);
        assertThat(result.getContent()).hasSize(1);
        verify(subscriptionPlanRepository).findAllByNameContainingIgnoreCase(search, pageable);
        verify(subscriptionPlanRepository, never()).findAll(any(Pageable.class));
    }

    @Test
    void getAllPlans_withNullSearch_returnsAllPlans() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<SubscriptionPlan> expectedPage = new PageImpl<>(List.of(createTestPlan()));

        when(subscriptionPlanRepository.findAll(pageable)).thenReturn(expectedPage);

        Page<SubscriptionPlan> result = subscriptionPlanService.getAllPlans(null, pageable);

        assertThat(result).isEqualTo(expectedPage);
        verify(subscriptionPlanRepository).findAll(pageable);
    }

    @Test
    void getAllPlans_withBlankSearch_returnsAllPlans() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<SubscriptionPlan> expectedPage = new PageImpl<>(List.of(createTestPlan()));

        when(subscriptionPlanRepository.findAll(pageable)).thenReturn(expectedPage);

        Page<SubscriptionPlan> result1 = subscriptionPlanService.getAllPlans("", pageable);
        Page<SubscriptionPlan> result2 = subscriptionPlanService.getAllPlans("   ", pageable);

        assertThat(result1).isEqualTo(expectedPage);
        assertThat(result2).isEqualTo(expectedPage);
        verify(subscriptionPlanRepository, times(2)).findAll(pageable);
        verify(subscriptionPlanRepository, never()).findAllByNameContainingIgnoreCase(anyString(),
            any());
    }

    @Test
    void getPlanById_returnsPlanWithMovies() {
        Long planId = 1L;
        Movie movie1 = createTestMovie(1L, "Movie 1");
        Movie movie2 = createTestMovie(2L, "Movie 2");
        Set<Movie> movies = new HashSet<>(List.of(movie1, movie2));
        SubscriptionPlan expectedPlan = createTestPlanWithMovies(movies);

        when(subscriptionPlanUtils.findByIdWithMovies(planId)).thenReturn(expectedPlan);

        SubscriptionPlan result = subscriptionPlanService.getPlanById(planId);

        assertThat(result).isEqualTo(expectedPlan);
        assertThat(result.getMovies()).hasSize(2);
        verify(subscriptionPlanUtils).findByIdWithMovies(planId);
    }

    @Test
    void getPlanById_callsUtilsMethod() {
        Long planId = 42L;
        SubscriptionPlan expectedPlan = createTestPlan();

        when(subscriptionPlanUtils.findByIdWithMovies(planId)).thenReturn(expectedPlan);

        SubscriptionPlan result = subscriptionPlanService.getPlanById(planId);

        assertThat(result).isEqualTo(expectedPlan);
        verify(subscriptionPlanUtils).findByIdWithMovies(planId);
    }

    @Test
    void createPlan_createsAndSavesPlan_returnsCreatedPlan() {
        CreateSubscriptionPlanRequest request = createTestRequest(null);
        SubscriptionPlan mappedPlan = createTestPlan();
        SubscriptionPlan savedPlan = createTestPlan();
        savedPlan.setId(10L);

        when(subscriptionPlanRepository.findByName(request.name())).thenReturn(Optional.empty());
        when(subscriptionPlanMapper.toEntity(request)).thenReturn(mappedPlan);
        when(subscriptionPlanRepository.save(mappedPlan)).thenReturn(savedPlan);

        SubscriptionPlan result = subscriptionPlanService.createPlan(request);

        assertThat(result).isEqualTo(savedPlan);
        assertThat(result.getId()).isEqualTo(10L);
        verify(subscriptionPlanRepository).findByName(request.name());
        verify(subscriptionPlanMapper).toEntity(request);
        verify(subscriptionPlanRepository).save(mappedPlan);
    }

    @Test
    void createPlan_withMovies_associatesMoviesToPlan() {
        Movie movie1 = createTestMovie(1L, "Movie 1");
        Movie movie2 = createTestMovie(2L, "Movie 2");
        List<Long> movieIds = List.of(1L, 2L);
        CreateSubscriptionPlanRequest request = createTestRequest(movieIds);

        SubscriptionPlan mappedPlan = createTestPlan();
        SubscriptionPlan savedPlan = createTestPlan();

        when(subscriptionPlanRepository.findByName(request.name())).thenReturn(Optional.empty());
        when(subscriptionPlanMapper.toEntity(request)).thenReturn(mappedPlan);
        when(movieRepository.findAllById(movieIds)).thenReturn(List.of(movie1, movie2));
        when(subscriptionPlanRepository.save(any(SubscriptionPlan.class))).thenReturn(savedPlan);

        SubscriptionPlan result = subscriptionPlanService.createPlan(request);

        assertThat(result).isEqualTo(savedPlan);
        verify(movieRepository).findAllById(movieIds);

        ArgumentCaptor<SubscriptionPlan> planCaptor = ArgumentCaptor.forClass(
            SubscriptionPlan.class);
        verify(subscriptionPlanRepository).save(planCaptor.capture());

        SubscriptionPlan capturedPlan = planCaptor.getValue();
        assertThat(capturedPlan.getMovies()).hasSize(2);
        assertThat(capturedPlan.getMovies()).extracting(Movie::getId)
            .containsExactlyInAnyOrder(1L, 2L);
    }

    @Test
    void createPlan_withoutMovies_createsEmptyMovieSet() {
        CreateSubscriptionPlanRequest request = createTestRequest(null);
        SubscriptionPlan mappedPlan = createTestPlan();
        SubscriptionPlan savedPlan = createTestPlan();

        when(subscriptionPlanRepository.findByName(request.name())).thenReturn(Optional.empty());
        when(subscriptionPlanMapper.toEntity(request)).thenReturn(mappedPlan);
        when(subscriptionPlanRepository.save(mappedPlan)).thenReturn(savedPlan);

        SubscriptionPlan result = subscriptionPlanService.createPlan(request);

        assertThat(result).isEqualTo(savedPlan);
        verify(subscriptionPlanRepository).save(mappedPlan);
        verify(movieRepository, never()).findAllById(anyList());
    }

    @Test
    void createPlan_whenPlanNameExists_throwsSubscriptionPlanAlreadyExistsException() {
        CreateSubscriptionPlanRequest request = createTestRequest(null);
        SubscriptionPlan existingPlan = createTestPlan();

        when(subscriptionPlanRepository.findByName(request.name())).thenReturn(
            Optional.of(existingPlan));

        assertThatThrownBy(() -> subscriptionPlanService.createPlan(request))
            .isInstanceOf(SubscriptionPlanAlreadyExistsException.class)
            .hasMessageContaining(request.name());

        verify(subscriptionPlanRepository).findByName(request.name());
        verify(subscriptionPlanMapper, never()).toEntity(any());
        verify(subscriptionPlanRepository, never()).save(any());
    }

    @Test
    void createPlan_whenMoviesNotFound_throwsMoviesNotFoundException() {
        List<Long> movieIds = List.of(1L, 2L, 3L);
        CreateSubscriptionPlanRequest request = createTestRequest(movieIds);
        SubscriptionPlan mappedPlan = createTestPlan();

        Movie movie1 = createTestMovie(1L, "Movie 1");

        when(subscriptionPlanRepository.findByName(request.name())).thenReturn(Optional.empty());
        when(subscriptionPlanMapper.toEntity(request)).thenReturn(mappedPlan);
        when(movieRepository.findAllById(movieIds)).thenReturn(List.of(movie1));

        assertThatThrownBy(() -> subscriptionPlanService.createPlan(request))
            .isInstanceOf(MoviesNotFoundException.class)
            .hasMessageContaining("2")
            .hasMessageContaining("3");

        verify(movieRepository).findAllById(movieIds);
        verify(subscriptionPlanRepository, never()).save(any());
    }

    @Test
    void updatePlan_updatesExistingPlan_returnsSavedPlan() {
        Long planId = 1L;
        CreateSubscriptionPlanRequest request = createTestRequest(null);
        SubscriptionPlan existingPlan = createTestPlan();
        SubscriptionPlan savedPlan = createTestPlan();

        when(subscriptionPlanUtils.findById(planId)).thenReturn(existingPlan);
        when(subscriptionPlanRepository.save(existingPlan)).thenReturn(savedPlan);

        SubscriptionPlan result = subscriptionPlanService.updatePlan(planId, request);

        assertThat(result).isEqualTo(savedPlan);
        verify(subscriptionPlanUtils).findById(planId);
        verify(subscriptionPlanMapper).updateEntityFromDto(request, existingPlan);
        verify(subscriptionPlanRepository).save(existingPlan);
    }

    @Test
    void updatePlan_withNewMovies_replacesMovieSet() {
        Long planId = 1L;
        Movie movie1 = createTestMovie(1L, "Movie 1");
        Movie movie2 = createTestMovie(2L, "Movie 2");
        List<Long> movieIds = List.of(1L, 2L);
        CreateSubscriptionPlanRequest request = createTestRequest(movieIds);

        SubscriptionPlan existingPlan = createTestPlan();
        SubscriptionPlan savedPlan = createTestPlan();

        when(subscriptionPlanUtils.findById(planId)).thenReturn(existingPlan);
        when(movieRepository.findAllById(movieIds)).thenReturn(List.of(movie1, movie2));
        when(subscriptionPlanRepository.save(existingPlan)).thenReturn(savedPlan);

        SubscriptionPlan result = subscriptionPlanService.updatePlan(planId, request);

        assertThat(result).isEqualTo(savedPlan);
        verify(movieRepository).findAllById(movieIds);
        verify(subscriptionPlanRepository).save(existingPlan);

        assertThat(existingPlan.getMovies()).hasSize(2);
        assertThat(existingPlan.getMovies()).extracting(Movie::getId)
            .containsExactlyInAnyOrder(1L, 2L);
    }

    @Test
    void updatePlan_callsMapperUpdateMethod() {
        Long planId = 1L;
        CreateSubscriptionPlanRequest request = createTestRequest(null);
        SubscriptionPlan existingPlan = createTestPlan();
        SubscriptionPlan savedPlan = createTestPlan();

        when(subscriptionPlanUtils.findById(planId)).thenReturn(existingPlan);
        when(subscriptionPlanRepository.save(existingPlan)).thenReturn(savedPlan);

        subscriptionPlanService.updatePlan(planId, request);

        verify(subscriptionPlanMapper).updateEntityFromDto(request, existingPlan);
    }

    @Test
    void updatePlan_whenMoviesNotFound_throwsMoviesNotFoundException() {
        Long planId = 1L;
        List<Long> movieIds = List.of(1L, 2L, 3L);
        CreateSubscriptionPlanRequest request = createTestRequest(movieIds);
        SubscriptionPlan existingPlan = createTestPlan();

        Movie movie1 = createTestMovie(1L, "Movie 1");

        when(subscriptionPlanUtils.findById(planId)).thenReturn(existingPlan);
        when(movieRepository.findAllById(movieIds)).thenReturn(List.of(movie1));

        assertThatThrownBy(() -> subscriptionPlanService.updatePlan(planId, request))
            .isInstanceOf(MoviesNotFoundException.class)
            .hasMessageContaining("2")
            .hasMessageContaining("3");

        verify(movieRepository).findAllById(movieIds);
        verify(subscriptionPlanRepository, never()).save(any());
    }

    @Test
    void addMoviesToPlan_addsMoviesToExistingPlan() {
        Long planId = 1L;
        List<Long> movieIds = List.of(1L, 2L);
        Movie movie1 = createTestMovie(1L, "Movie 1");
        Movie movie2 = createTestMovie(2L, "Movie 2");

        SubscriptionPlan existingPlan = createTestPlan();
        existingPlan.setMovies(new HashSet<>());
        SubscriptionPlan savedPlan = createTestPlanWithMovies(
            new HashSet<>(List.of(movie1, movie2)));

        when(subscriptionPlanUtils.findById(planId)).thenReturn(existingPlan);
        when(movieRepository.findAllById(movieIds)).thenReturn(List.of(movie1, movie2));
        when(subscriptionPlanRepository.save(existingPlan)).thenReturn(savedPlan);

        SubscriptionPlan result = subscriptionPlanService.addMoviesToPlan(planId, movieIds);

        assertThat(result).isEqualTo(savedPlan);
        verify(subscriptionPlanUtils).findById(planId);
        verify(movieRepository).findAllById(movieIds);
        verify(subscriptionPlanRepository).save(existingPlan);

        assertThat(existingPlan.getMovies()).hasSize(2);
        assertThat(existingPlan.getMovies()).containsExactlyInAnyOrder(movie1, movie2);
    }

    @Test
    void addMoviesToPlan_whenMoviesNotFound_throwsMoviesNotFoundException() {
        Long planId = 1L;
        List<Long> movieIds = List.of(1L, 2L, 99L);
        Movie movie1 = createTestMovie(1L, "Movie 1");
        Movie movie2 = createTestMovie(2L, "Movie 2");

        SubscriptionPlan existingPlan = createTestPlan();

        when(subscriptionPlanUtils.findById(planId)).thenReturn(existingPlan);
        when(movieRepository.findAllById(movieIds)).thenReturn(List.of(movie1, movie2));

        assertThatThrownBy(() -> subscriptionPlanService.addMoviesToPlan(planId, movieIds))
            .isInstanceOf(MoviesNotFoundException.class)
            .hasMessageContaining("99");

        verify(movieRepository).findAllById(movieIds);
        verify(subscriptionPlanRepository, never()).save(any());
    }

    @Test
    void removeMoviesFromPlan_removesMoviesFromPlan_returnsSavedPlan() {
        Long planId = 1L;
        Movie movie1 = createTestMovie(1L, "Movie 1");
        Movie movie2 = createTestMovie(2L, "Movie 2");
        Movie movie3 = createTestMovie(3L, "Movie 3");

        List<Long> movieIdsToRemove = List.of(1L, 2L);

        Set<Movie> movies = new HashSet<>(List.of(movie1, movie2, movie3));
        SubscriptionPlan existingPlan = createTestPlanWithMovies(movies);
        SubscriptionPlan savedPlan = createTestPlanWithMovies(new HashSet<>(List.of(movie3)));

        when(subscriptionPlanUtils.findById(planId)).thenReturn(existingPlan);
        when(subscriptionPlanRepository.save(existingPlan)).thenReturn(savedPlan);

        SubscriptionPlan result = subscriptionPlanService.removeMoviesFromPlan(planId,
            movieIdsToRemove);

        assertThat(result).isEqualTo(savedPlan);
        verify(subscriptionPlanUtils).findById(planId);
        verify(subscriptionPlanRepository).save(existingPlan);

        assertThat(existingPlan.getMovies()).hasSize(1);
        assertThat(existingPlan.getMovies()).extracting(Movie::getId).containsExactly(3L);
    }

    @Test
    void removeMoviesFromPlan_whenNoMoviesRemoved_throwsMoviesNotInPlanException() {
        Long planId = 1L;
        Movie movie1 = createTestMovie(1L, "Movie 1");
        Movie movie2 = createTestMovie(2L, "Movie 2");

        List<Long> movieIdsToRemove = List.of(99L, 100L);

        Set<Movie> movies = new HashSet<>(List.of(movie1, movie2));
        SubscriptionPlan existingPlan = createTestPlanWithMovies(movies);

        when(subscriptionPlanUtils.findById(planId)).thenReturn(existingPlan);
        assertThatThrownBy(
            () -> subscriptionPlanService.removeMoviesFromPlan(planId, movieIdsToRemove))
            .isInstanceOf(MoviesNotInPlanException.class)
            .hasMessage("None of the provided movies were found in the plan");

        verify(subscriptionPlanUtils).findById(planId);
        verify(subscriptionPlanRepository, never()).save(any());
    }

    @Test
    void deletePlan_cancelsSubscriptionsAndDeletesPlan() {
        Long planId = 1L;
        SubscriptionPlan planToDelete = createTestPlan();

        when(subscriptionPlanUtils.findByIdWithLock(planId)).thenReturn(planToDelete);

        subscriptionPlanService.deletePlan(planId);

        verify(subscriptionPlanUtils).findByIdWithLock(planId);
        verify(userSubscriptionRepository).cancelAllByPlan(planToDelete);
        verify(subscriptionPlanRepository).delete(planToDelete);
    }

    @Test
    void deletePlan_usesLockForConcurrency() {
        Long planId = 1L;
        SubscriptionPlan planToDelete = createTestPlan();

        when(subscriptionPlanUtils.findByIdWithLock(planId)).thenReturn(planToDelete);

        subscriptionPlanService.deletePlan(planId);

        verify(subscriptionPlanUtils).findByIdWithLock(planId);
        verify(subscriptionPlanUtils, never()).findById(planId);
    }
}

