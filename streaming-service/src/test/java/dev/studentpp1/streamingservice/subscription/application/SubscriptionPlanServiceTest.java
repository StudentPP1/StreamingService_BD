package dev.studentpp1.streamingservice.subscription.application;

import dev.studentpp1.streamingservice.subscription.application.usecase.SubscriptionPlanService;
import dev.studentpp1.streamingservice.subscription.domain.exception.MoviesNotInPlanException;
import dev.studentpp1.streamingservice.subscription.domain.exception.SubscriptionPlanNotFoundException;
import dev.studentpp1.streamingservice.subscription.domain.factory.SubscriptionPlanFactory;
import dev.studentpp1.streamingservice.subscription.domain.model.SubscriptionMovie;
import dev.studentpp1.streamingservice.subscription.domain.model.SubscriptionPlan;
import dev.studentpp1.streamingservice.subscription.domain.port.MovieProvider;
import dev.studentpp1.streamingservice.subscription.domain.repository.SubscriptionPlanRepository;
import dev.studentpp1.streamingservice.subscription.domain.repository.UserSubscriptionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class SubscriptionPlanServiceTest {

    private SubscriptionPlanRepository planRepository;
    private UserSubscriptionRepository subscriptionRepository;
    private MovieProvider movieProvider;
    private SubscriptionPlanFactory planFactory;
    private SubscriptionPlanService planService;

    @BeforeEach
    void setUp() {
        planRepository = mock(SubscriptionPlanRepository.class);
        subscriptionRepository = mock(UserSubscriptionRepository.class);
        movieProvider = mock(MovieProvider.class);
        planFactory = mock(SubscriptionPlanFactory.class);
        planService = new SubscriptionPlanService(
                planRepository, subscriptionRepository, movieProvider, planFactory);
    }

    @Test
    void getPlanById_exists_returnsPlanWithMovies() {
        SubscriptionPlan plan = SubscriptionPlan.restore(
                1L, "Basic", "desc", BigDecimal.valueOf(9.99), 30, Set.of(10L), 0L);
        SubscriptionMovie movie = new SubscriptionMovie(10L, "Inception", "desc", 2010, BigDecimal.valueOf(8.8));

        when(planRepository.findByIdWithMovies(1L)).thenReturn(Optional.of(plan));
        when(movieProvider.findAllById(List.of(10L))).thenReturn(List.of(movie));

        var result = planService.getPlanById(1L);

        assertThat(result.plan().getName()).isEqualTo("Basic");
        assertThat(result.movies()).hasSize(1);
        assertThat(result.movies().get(0).title()).isEqualTo("Inception");
    }

    @Test
    void getPlanById_notFound_throwsSubscriptionPlanNotFoundException() {
        when(planRepository.findByIdWithMovies(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> planService.getPlanById(99L))
                .isInstanceOf(SubscriptionPlanNotFoundException.class);
    }

    @Test
    void deletePlan_exists_cancelsSubscriptionsAndDeletes() {
        SubscriptionPlan plan = SubscriptionPlan.restore(
                1L, "Basic", "desc", BigDecimal.valueOf(9.99), 30, Set.of(), 0L);

        when(planRepository.findByIdWithLock(1L)).thenReturn(Optional.of(plan));

        planService.deletePlan(1L);

        verify(subscriptionRepository).cancelAllByPlanId(1L);
        verify(planRepository).delete(plan);
    }

    @Test
    void deletePlan_notFound_throwsSubscriptionPlanNotFoundException() {
        when(planRepository.findByIdWithLock(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> planService.deletePlan(99L))
                .isInstanceOf(SubscriptionPlanNotFoundException.class);
    }

    @Test
    void addMoviesToPlan_validIds_addsMovies() {
        SubscriptionPlan plan = SubscriptionPlan.restore(
                1L, "Basic", "desc", BigDecimal.valueOf(9.99), 30, Set.of(), 0L);
        SubscriptionMovie movie = new SubscriptionMovie(5L, "Movie", "desc", 2020, BigDecimal.valueOf(7.0));

        when(planRepository.findById(1L)).thenReturn(Optional.of(plan));
        when(movieProvider.findAllById(List.of(5L))).thenReturn(List.of(movie));
        when(planRepository.save(plan)).thenReturn(plan);
        when(movieProvider.findAllById(anyList())).thenReturn(List.of(movie));

        var result = planService.addMoviesToPlan(1L, List.of(5L));

        assertThat(result.plan().getMovieIds()).contains(5L);
        verify(planRepository).save(plan);
    }

    @Test
    void removeMoviesFromPlan_existingIds_removesMovies() {
        SubscriptionPlan plan = SubscriptionPlan.restore(
                1L, "Basic", "desc", BigDecimal.valueOf(9.99), 30, Set.of(5L, 6L), 0L);

        when(planRepository.findById(1L)).thenReturn(Optional.of(plan));
        when(planRepository.save(plan)).thenReturn(plan);
        when(movieProvider.findAllById(anyList())).thenReturn(List.of());

        var result = planService.removeMoviesFromPlan(1L, List.of(5L));

        assertThat(result.plan().getMovieIds()).doesNotContain(5L);
    }

    @Test
    void removeMoviesFromPlan_notPresentIds_throwsMoviesNotInPlanException() {
        SubscriptionPlan plan = SubscriptionPlan.restore(
                1L, "Basic", "desc", BigDecimal.valueOf(9.99), 30, Set.of(1L), 0L);

        when(planRepository.findById(1L)).thenReturn(Optional.of(plan));

        assertThatThrownBy(() -> planService.removeMoviesFromPlan(1L, List.of(99L)))
                .isInstanceOf(MoviesNotInPlanException.class);
    }
}