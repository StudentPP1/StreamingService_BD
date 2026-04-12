package dev.studentpp1.streamingservice.subscription.application.query;

import dev.studentpp1.streamingservice.common.dto.PageResult;
import dev.studentpp1.streamingservice.subscription.application.query.readmodel.UserSubscriptionWithPlanReadModel;
import dev.studentpp1.streamingservice.subscription.application.query.repo.UserSubscriptionReadRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SubscriptionQueryHandler {

    private final UserSubscriptionReadRepository userSubscriptionReadRepository;

    public PageResult<UserSubscriptionWithPlanReadModel> handle(GetMySubscriptionsQuery query) {
        return userSubscriptionReadRepository.findAllByUserId(query.userId(), query.page(), query.size());
    }
}

