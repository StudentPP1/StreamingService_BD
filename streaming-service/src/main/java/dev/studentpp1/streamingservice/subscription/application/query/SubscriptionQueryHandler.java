package dev.studentpp1.streamingservice.subscription.application.query;

import dev.studentpp1.streamingservice.common.dto.PageResult;
import dev.studentpp1.streamingservice.subscription.application.usecase.SubscriptionService;
import dev.studentpp1.streamingservice.subscription.application.usecase.SubscriptionService.UserSubscriptionWithPlan;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SubscriptionQueryHandler {

    private final SubscriptionService subscriptionService;

    public PageResult<UserSubscriptionWithPlan> handle(GetMySubscriptionsQuery query) {
        return subscriptionService.getUserSubscriptionsWithPlan(query.userId(), query.page(), query.size());
    }
}

