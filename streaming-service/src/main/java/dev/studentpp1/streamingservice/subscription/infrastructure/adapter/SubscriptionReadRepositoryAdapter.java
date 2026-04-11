package dev.studentpp1.streamingservice.subscription.infrastructure.adapter;

import dev.studentpp1.streamingservice.common.dto.PageResult;
import dev.studentpp1.streamingservice.movies.infrastructure.repository.MovieJpaRepository;
import dev.studentpp1.streamingservice.subscription.application.read.SubscriptionReadRepository;
import dev.studentpp1.streamingservice.subscription.infrastructure.repository.SubscriptionPlanJpaRepository;
import dev.studentpp1.streamingservice.subscription.infrastructure.repository.UserSubscriptionJpaRepository;
import dev.studentpp1.streamingservice.subscription.presentation.dto.response.SubscriptionPlanDetailsDto;
import dev.studentpp1.streamingservice.subscription.presentation.dto.response.SubscriptionPlanMovieDto;
import dev.studentpp1.streamingservice.subscription.presentation.dto.response.SubscriptionPlanSummaryDto;
import dev.studentpp1.streamingservice.subscription.presentation.dto.response.UserSubscriptionDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SubscriptionReadRepositoryAdapter implements SubscriptionReadRepository {

    private final SubscriptionPlanJpaRepository subscriptionPlanJpaRepository;
    private final UserSubscriptionJpaRepository userSubscriptionJpaRepository;
    private final MovieJpaRepository movieJpaRepository;

    @Override
    public PageResult<SubscriptionPlanSummaryDto> findAllPlans(String search, int page, int size) {
        var pageRequest = PageRequest.of(page, size);
        var result = (search == null || search.isBlank())
                ? subscriptionPlanJpaRepository.findAll(pageRequest)
                : subscriptionPlanJpaRepository.findAllByNameContainingIgnoreCase(search, pageRequest);

        return new PageResult<>(
                result.getContent().stream()
                        .map(plan -> new SubscriptionPlanSummaryDto(
                                plan.getId(),
                                plan.getName(),
                                plan.getDescription(),
                                plan.getPrice(),
                                plan.getDuration()
                        ))
                        .toList(),
                result.getNumber(),
                result.getSize(),
                result.getTotalElements(),
                result.getTotalPages()
        );
    }

    @Override
    public java.util.Optional<SubscriptionPlanDetailsDto> findPlanById(Long id) {
        return subscriptionPlanJpaRepository.findWithMoviesById(id)
                .map(plan -> {
                    Set<SubscriptionPlanMovieDto> movies = movieJpaRepository.findAllById(plan.getMovieIds()).stream()
                            .map(movie -> new SubscriptionPlanMovieDto(
                                    movie.getId(),
                                    movie.getTitle(),
                                    movie.getDescription(),
                                    movie.getYear(),
                                    movie.getRating()
                            ))
                            .collect(Collectors.toSet());

                    return new SubscriptionPlanDetailsDto(
                            plan.getId(),
                            plan.getName(),
                            plan.getDescription(),
                            plan.getPrice(),
                            plan.getDuration(),
                            movies
                    );
                });
    }

    @Override
    public PageResult<UserSubscriptionDto> findUserSubscriptions(Long userId, int page, int size) {
        var result = userSubscriptionJpaRepository.findAllByUserId(userId, PageRequest.of(page, size));

        return new PageResult<>(
                result.getContent().stream()
                        .map(sub -> new UserSubscriptionDto(
                                sub.getId(),
                                sub.getStartTime(),
                                sub.getEndTime(),
                                sub.getStatus(),
                                sub.getPlan() != null ? sub.getPlan().getName() : "Unknown"
                        ))
                        .toList(),
                result.getNumber(),
                result.getSize(),
                result.getTotalElements(),
                result.getTotalPages()
        );
    }
}

