package dev.studentpp1.streamingservice.subscription.infrastructure.adapter;

import dev.studentpp1.streamingservice.common.dto.PageResult;
import dev.studentpp1.streamingservice.subscription.application.query.repo.SubscriptionPlanReadRepository;
import dev.studentpp1.streamingservice.subscription.application.query.readmodel.SubscriptionPlanWithMoviesReadModel;
import dev.studentpp1.streamingservice.subscription.domain.model.SubscriptionMovie;
import dev.studentpp1.streamingservice.subscription.domain.model.SubscriptionPlan;
import dev.studentpp1.streamingservice.subscription.domain.port.MovieProvider;
import dev.studentpp1.streamingservice.subscription.infrastructure.mapper.SubscriptionPlanPersistenceMapper;
import dev.studentpp1.streamingservice.subscription.infrastructure.repository.SubscriptionPlanJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class SubscriptionPlanReadRepositoryAdapter implements SubscriptionPlanReadRepository {

    private final SubscriptionPlanJpaRepository subscriptionPlanJpaRepository;
    private final SubscriptionPlanPersistenceMapper subscriptionPlanPersistenceMapper;
    private final MovieProvider movieProvider;

    @Override
    public PageResult<SubscriptionPlan> findAll(String search, int page, int size) {
        Page<dev.studentpp1.streamingservice.subscription.infrastructure.entity.SubscriptionPlanEntity> result;
        if (search == null || search.isBlank()) {
            result = subscriptionPlanJpaRepository.findAll(PageRequest.of(page, size));
        } else {
            result = subscriptionPlanJpaRepository.findAllByNameContainingIgnoreCase(search, PageRequest.of(page, size));
        }

        return new PageResult<>(
                result.getContent().stream()
                        .map(this::toSummaryDomain)
                        .toList(),
                result.getNumber(),
                result.getSize(),
                result.getTotalElements(),
                result.getTotalPages()
        );
    }

    @Override
    public Optional<SubscriptionPlanWithMoviesReadModel> findById(Long id) {
        return subscriptionPlanJpaRepository.findWithMoviesById(id)
                .map(subscriptionPlanPersistenceMapper::toDomain)
                .map(this::toReadModel);
    }

    private SubscriptionPlanWithMoviesReadModel toReadModel(SubscriptionPlan plan) {
        List<SubscriptionMovie> movies = movieProvider.findAllById(new ArrayList<>(plan.getMovieIds()));
        return new SubscriptionPlanWithMoviesReadModel(plan, movies);
    }

    private SubscriptionPlan toSummaryDomain(dev.studentpp1.streamingservice.subscription.infrastructure.entity.SubscriptionPlanEntity entity) {
        return SubscriptionPlan.restore(
                entity.getId(),
                entity.getName(),
                entity.getDescription(),
                entity.getPrice(),
                entity.getDuration(),
                new HashSet<>(),
                entity.getVersion()
        );
    }
}


