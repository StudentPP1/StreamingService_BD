package dev.studentpp1.streamingservice.subscription.application.read;

import dev.studentpp1.streamingservice.common.dto.PageResult;
import dev.studentpp1.streamingservice.subscription.presentation.dto.response.SubscriptionPlanDetailsDto;
import dev.studentpp1.streamingservice.subscription.presentation.dto.response.SubscriptionPlanSummaryDto;
import dev.studentpp1.streamingservice.subscription.presentation.dto.response.UserSubscriptionDto;

import java.util.Optional;

public interface SubscriptionReadRepository {
    PageResult<SubscriptionPlanSummaryDto> findAllPlans(String search, int page, int size);

    Optional<SubscriptionPlanDetailsDto> findPlanById(Long id);

    PageResult<UserSubscriptionDto> findUserSubscriptions(Long userId, int page, int size);
}

