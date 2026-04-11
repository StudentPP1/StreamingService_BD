package dev.studentpp1.streamingservice.subscription.application.cqs;

import dev.studentpp1.streamingservice.common.dto.PageResult;
import dev.studentpp1.streamingservice.subscription.application.cqs.SubscriptionCqs.*;
import dev.studentpp1.streamingservice.subscription.application.read.SubscriptionReadRepository;
import dev.studentpp1.streamingservice.subscription.domain.exception.SubscriptionPlanNotFoundException;
import dev.studentpp1.streamingservice.subscription.presentation.dto.response.SubscriptionPlanDetailsDto;
import dev.studentpp1.streamingservice.subscription.presentation.dto.response.SubscriptionPlanSummaryDto;
import dev.studentpp1.streamingservice.subscription.presentation.dto.response.UserSubscriptionDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class SubscriptionQueryHandler {
    private final SubscriptionReadRepository subscriptionReadRepository;

    public PageResult<SubscriptionPlanSummaryDto> handle(GetAllPlansQuery query) {
        return subscriptionReadRepository.findAllPlans(query.search(), query.page(), query.size());
    }

    public SubscriptionPlanDetailsDto handle(GetPlanByIdQuery query) {
        return subscriptionReadRepository.findPlanById(query.id())
                .orElseThrow(() -> new SubscriptionPlanNotFoundException(query.id()));
    }

    public PageResult<UserSubscriptionDto> handle(GetMySubscriptionsQuery query) {
        return subscriptionReadRepository.findUserSubscriptions(query.userId(), query.page(), query.size());
    }
}
