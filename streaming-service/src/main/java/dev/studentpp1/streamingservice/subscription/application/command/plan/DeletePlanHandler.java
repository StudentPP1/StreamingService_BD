package dev.studentpp1.streamingservice.subscription.application.command.plan;

import dev.studentpp1.streamingservice.subscription.domain.exception.SubscriptionPlanNotFoundException;
import dev.studentpp1.streamingservice.subscription.domain.model.SubscriptionPlan;
import dev.studentpp1.streamingservice.subscription.domain.repository.SubscriptionPlanRepository;
import dev.studentpp1.streamingservice.subscription.domain.repository.UserSubscriptionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class DeletePlanHandler {

    private final SubscriptionPlanRepository subscriptionPlanRepository;
    private final UserSubscriptionRepository userSubscriptionRepository;

    @Transactional
    public void handle(DeletePlanCommand command) {
        SubscriptionPlan plan = subscriptionPlanRepository.findByIdWithLock(command.id())
                .orElseThrow(() -> new SubscriptionPlanNotFoundException(command.id()));
        userSubscriptionRepository.cancelAllByPlanId(plan.getId());
        subscriptionPlanRepository.delete(plan);
    }
}

