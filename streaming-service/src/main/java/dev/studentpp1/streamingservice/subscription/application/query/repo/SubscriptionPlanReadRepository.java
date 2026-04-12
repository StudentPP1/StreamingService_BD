package dev.studentpp1.streamingservice.subscription.application.query.repo;

import dev.studentpp1.streamingservice.common.dto.PageResult;
import dev.studentpp1.streamingservice.subscription.application.query.readmodel.SubscriptionPlanWithMoviesReadModel;
import dev.studentpp1.streamingservice.subscription.domain.model.SubscriptionPlan;

import java.util.Optional;

public interface SubscriptionPlanReadRepository {

    PageResult<SubscriptionPlan> findAll(String search, int page, int size);

    Optional<SubscriptionPlanWithMoviesReadModel> findById(Long id);
}

