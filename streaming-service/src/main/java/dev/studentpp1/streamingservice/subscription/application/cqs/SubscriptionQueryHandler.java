package dev.studentpp1.streamingservice.subscription.application.cqs;
import dev.studentpp1.streamingservice.common.dto.PageResult;
import dev.studentpp1.streamingservice.subscription.application.cqs.SubscriptionCqs.*;
import dev.studentpp1.streamingservice.subscription.application.usecase.SubscriptionPlanService;
import dev.studentpp1.streamingservice.subscription.application.usecase.SubscriptionService;
import dev.studentpp1.streamingservice.subscription.domain.model.SubscriptionPlan;
import dev.studentpp1.streamingservice.subscription.domain.model.UserSubscription;
import dev.studentpp1.streamingservice.subscription.presentation.dto.response.SubscriptionPlanDetailsDto;
import dev.studentpp1.streamingservice.subscription.presentation.dto.response.SubscriptionPlanSummaryDto;
import dev.studentpp1.streamingservice.subscription.presentation.dto.response.UserSubscriptionDto;
import dev.studentpp1.streamingservice.subscription.presentation.mapper.SubscriptionPlanPresentationMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
@Service
@RequiredArgsConstructor
public class SubscriptionQueryHandler {
    private final SubscriptionPlanService subscriptionPlanService;
    private final SubscriptionService subscriptionService;
    private final SubscriptionPlanPresentationMapper mapper;
    public PageResult<SubscriptionPlanSummaryDto> handle(GetAllPlansQuery query) {
        PageResult<SubscriptionPlan> result = subscriptionPlanService.getAllPlans(query.search(), query.page(), query.size());
        List<SubscriptionPlanSummaryDto> content = result.content().stream().map(mapper::toSummaryDto).toList();
        return new PageResult<>(content, result.page(), result.size(), result.totalElements(), result.totalPages());
    }
    public SubscriptionPlanDetailsDto handle(GetPlanByIdQuery query) {
        var result = subscriptionPlanService.getPlanById(query.id());
        return mapper.toDetailsDto(result.plan(), result.movies());
    }
    public PageResult<UserSubscriptionDto> handle(GetMySubscriptionsQuery query) {
        PageResult<SubscriptionService.UserSubscriptionWithPlan> result =
                subscriptionService.getUserSubscriptionsWithPlan(query.userId(), query.page(), query.size());
        List<UserSubscriptionDto> content = result.content().stream()
                .map(item -> {
                    UserSubscription subscription = item.subscription();
                    return new UserSubscriptionDto(
                            subscription.getId(),
                            subscription.getStartTime(),
                            subscription.getEndTime(),
                            subscription.getStatus(),
                            item.planName()
                    );
                })
                .toList();
        return new PageResult<>(content, result.page(), result.size(), result.totalElements(), result.totalPages());
    }
}
