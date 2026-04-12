package dev.studentpp1.streamingservice.subscription.application.usecase;

import dev.studentpp1.streamingservice.common.dto.PageResult;
import dev.studentpp1.streamingservice.subscription.domain.exception.MoviesNotFoundException;
import dev.studentpp1.streamingservice.subscription.domain.exception.MoviesNotInPlanException;
import dev.studentpp1.streamingservice.subscription.domain.exception.SubscriptionPlanNotFoundException;
import dev.studentpp1.streamingservice.subscription.domain.factory.SubscriptionPlanFactory;
import dev.studentpp1.streamingservice.subscription.domain.model.SubscriptionMovie;
import dev.studentpp1.streamingservice.subscription.domain.model.SubscriptionPlan;
import dev.studentpp1.streamingservice.subscription.domain.port.MovieProvider;
import dev.studentpp1.streamingservice.subscription.domain.repository.SubscriptionPlanRepository;
import dev.studentpp1.streamingservice.subscription.domain.repository.UserSubscriptionRepository;
import dev.studentpp1.streamingservice.subscription.application.dto.CreateSubscriptionPlanRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SubscriptionPlanService {

    private final SubscriptionPlanRepository subscriptionPlanRepository;
    private final UserSubscriptionRepository userSubscriptionRepository;
    private final MovieProvider movieProvider;
    private final SubscriptionPlanFactory subscriptionPlanFactory;

    @Transactional(readOnly = true)
    public PageResult<SubscriptionPlan> getAllPlans(String search, int page, int size) {
        if (search == null || search.isBlank()) {
            return subscriptionPlanRepository.findAll(page, size);
        }
        return subscriptionPlanRepository.findAllByNameContaining(search, page, size);
    }

    @Transactional(readOnly = true)
    public PlanWithMovies getPlanById(Long id) {
        SubscriptionPlan plan = subscriptionPlanRepository.findByIdWithMovies(id)
                .orElseThrow(() -> new SubscriptionPlanNotFoundException(id));
        List<SubscriptionMovie> movies = movieProvider.findAllById(new ArrayList<>(plan.getMovieIds()));
        return new PlanWithMovies(plan, movies);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public PlanWithMovies createPlan(CreateSubscriptionPlanRequest request) {
        SubscriptionPlan plan = subscriptionPlanFactory.create(
                request.name(), request.description(),
                request.price(), request.duration());

        List<SubscriptionMovie> movies = List.of();
        if (request.includedMovieIds() != null && !request.includedMovieIds().isEmpty()) {
            Set<Long> movieIds = validateAndGetMovieIds(request.includedMovieIds());
            plan.setMovieIds(movieIds);
            movies = movieProvider.findAllById(new ArrayList<>(movieIds));
        }

        return new PlanWithMovies(subscriptionPlanRepository.save(plan), movies);
    }
    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public PlanWithMovies updatePlan(Long id, CreateSubscriptionPlanRequest request) {
        SubscriptionPlan plan = subscriptionPlanRepository.findById(id)
                .orElseThrow(() -> new SubscriptionPlanNotFoundException(id));

        plan.update(request.name(), request.description(),
                request.price(), request.duration());

        List<SubscriptionMovie> movies = List.of();
        if (request.includedMovieIds() != null && !request.includedMovieIds().isEmpty()) {
            Set<Long> movieIds = validateAndGetMovieIds(request.includedMovieIds());
            plan.setMovieIds(movieIds);
            movies = movieProvider.findAllById(new ArrayList<>(movieIds));
        }

        return new PlanWithMovies(subscriptionPlanRepository.save(plan), movies);
    }
    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public PlanWithMovies addMoviesToPlan(Long planId, List<Long> movieIds) {
        SubscriptionPlan plan = subscriptionPlanRepository.findById(planId)
                .orElseThrow(() -> new SubscriptionPlanNotFoundException(planId));

        plan.addMovies(validateAndGetMovieIds(movieIds));
        SubscriptionPlan saved = subscriptionPlanRepository.save(plan);
        return new PlanWithMovies(saved,
                movieProvider.findAllById(new ArrayList<>(saved.getMovieIds())));
    }
    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public PlanWithMovies removeMoviesFromPlan(Long planId, List<Long> movieIds) {
        SubscriptionPlan plan = subscriptionPlanRepository.findById(planId)
                .orElseThrow(() -> new SubscriptionPlanNotFoundException(planId));

        if (!plan.removeMovies(new HashSet<>(movieIds)))
            throw new MoviesNotInPlanException();

        SubscriptionPlan saved = subscriptionPlanRepository.save(plan);
        return new PlanWithMovies(saved,
                movieProvider.findAllById(new ArrayList<>(saved.getMovieIds())));
    }
    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public void deletePlan(Long id) {
        SubscriptionPlan plan = subscriptionPlanRepository.findByIdWithLock(id)
                .orElseThrow(() -> new SubscriptionPlanNotFoundException(id));
        userSubscriptionRepository.cancelAllByPlanId(plan.getId());
        subscriptionPlanRepository.delete(plan);
    }

    private Set<Long> validateAndGetMovieIds(List<Long> movieIds) {
        List<SubscriptionMovie> movies = movieProvider.findAllById(movieIds);
        if (movies.size() != new HashSet<>(movieIds).size()) {
            List<Long> foundIds = movies.stream().map(SubscriptionMovie::id).toList();
            List<Long> missingIds = movieIds.stream()
                    .filter(id -> !foundIds.contains(id)).toList();
            throw new MoviesNotFoundException(missingIds);
        }
        return movies.stream().map(SubscriptionMovie::id).collect(Collectors.toSet());
    }

    public record PlanWithMovies(SubscriptionPlan plan, List<SubscriptionMovie> movies) {}
}