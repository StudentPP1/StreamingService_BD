package dev.studentpp1.streamingservice.subscription.application.query.repo;

import dev.studentpp1.streamingservice.common.dto.PageResult;
import dev.studentpp1.streamingservice.subscription.application.query.readmodel.UserSubscriptionWithPlanReadModel;

public interface UserSubscriptionReadRepository {

    PageResult<UserSubscriptionWithPlanReadModel> findAllByUserId(Long userId, int page, int size);
}


