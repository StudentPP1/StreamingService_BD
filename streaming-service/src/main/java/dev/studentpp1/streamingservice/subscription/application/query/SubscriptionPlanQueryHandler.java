package dev.studentpp1.streamingservice.subscription.application.query;

import dev.studentpp1.streamingservice.common.dto.PageResult;
import dev.studentpp1.streamingservice.subscription.application.usecase.SubscriptionPlanService;
import dev.studentpp1.streamingservice.subscription.application.usecase.SubscriptionPlanService.PlanWithMovies;
import dev.studentpp1.streamingservice.subscription.domain.model.SubscriptionPlan;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SubscriptionPlanQueryHandler {

    private final SubscriptionPlanService subscriptionPlanService;

    public PageResult<SubscriptionPlan> handle(GetAllPlansQuery query) {
        return subscriptionPlanService.getAllPlans(query.search(), query.page(), query.size());
    }

    public PlanWithMovies handle(GetPlanByIdQuery query) {
        return subscriptionPlanService.getPlanById(query.id());
    }
}

