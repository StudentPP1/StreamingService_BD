package dev.studentpp1.streamingservice.subscription.service;

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
import java.util.HashSet;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SubscriptionPlanService {

    private final SubscriptionPlanRepository subscriptionPlanRepository;
    private final UserSubscriptionRepository userSubscriptionRepository;
    private final MovieRepository movieRepository;
    private final SubscriptionPlanMapper subscriptionPlanMapper;
    private final SubscriptionPlanUtils subscriptionPlanUtils;

    public Page<SubscriptionPlan> getAllPlans(String search, Pageable pageable) {
        if (search == null || search.isBlank()) {
            return subscriptionPlanRepository.findAll(pageable);
        } else {
            return subscriptionPlanRepository.findAllByNameContainingIgnoreCase(search, pageable);
        }
    }

    public SubscriptionPlan getPlanById(Long id) {
        return subscriptionPlanUtils.findByIdWithMovies(id);
    }

    @Transactional
    public SubscriptionPlan createPlan(CreateSubscriptionPlanRequest request) {
        if (subscriptionPlanRepository.findByName(request.name()).isPresent()) {
            throw new SubscriptionPlanAlreadyExistsException(request.name());
        }

        SubscriptionPlan plan = subscriptionPlanMapper.toEntity(request);

        if (request.includedMovieIds() != null) {
            List<Movie> movies = getMovies(request.includedMovieIds());
            plan.setMovies(new HashSet<>(movies));
        }

        return subscriptionPlanRepository.save(plan);
    }

    @Transactional
    public SubscriptionPlan updatePlan(Long id, CreateSubscriptionPlanRequest request) {
        SubscriptionPlan plan = subscriptionPlanUtils.findById(id);

        subscriptionPlanMapper.updateEntityFromDto(request, plan);

        if (request.includedMovieIds() != null) {
            List<Movie> movies = getMovies(request.includedMovieIds());
            plan.setMovies(new HashSet<>(movies));
        }

        return subscriptionPlanRepository.save(plan);
    }

    @Transactional
    public SubscriptionPlan addMoviesToPlan(Long planId, List<Long> movieIds) {
        SubscriptionPlan plan = subscriptionPlanUtils.findById(planId);
        List<Movie> newMovies = getMovies(movieIds);

        plan.getMovies().addAll(newMovies);

        return subscriptionPlanRepository.save(plan);
    }

    private List<Movie> getMovies(List<Long> movieIds) {
        List<Movie> newMovies = movieRepository.findAllById(movieIds);

        if (newMovies.size() != new HashSet<>(movieIds).size()) {
            List<Long> foundIds = newMovies.stream().map(Movie::getId).toList();
            List<Long> missingIds = movieIds.stream()
                .filter(id -> !foundIds.contains(id))
                .toList();
            throw new MoviesNotFoundException(missingIds);
        }

        return newMovies;
    }

    @Transactional
    public void deletePlan(Long id) {
        SubscriptionPlan plan = subscriptionPlanUtils.findByIdWithLock(id);

        userSubscriptionRepository.cancelAllByPlan(plan);

        subscriptionPlanRepository.delete(plan);
    }


    // TODO: choose removal strategy (Idempotency or Strictness)
    @Transactional
    public SubscriptionPlan removeMoviesFromPlan(Long planId, List<Long> movieIds) {
        SubscriptionPlan plan = subscriptionPlanUtils.findById(planId);

        boolean changed = plan.getMovies().removeIf(movie -> movieIds.contains(movie.getId()));

        if (!changed) {
            throw new MoviesNotInPlanException();
        }

        return subscriptionPlanRepository.save(plan);
    }
}
