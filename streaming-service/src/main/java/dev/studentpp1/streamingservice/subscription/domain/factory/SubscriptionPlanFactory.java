package dev.studentpp1.streamingservice.subscription.domain.factory;

import dev.studentpp1.streamingservice.subscription.domain.exception.SubscriptionDomainException;
import dev.studentpp1.streamingservice.subscription.domain.exception.SubscriptionPlanAlreadyExistsException;
import dev.studentpp1.streamingservice.subscription.domain.model.SubscriptionPlan;
import dev.studentpp1.streamingservice.subscription.domain.repository.SubscriptionPlanRepository;

import java.math.BigDecimal;

public class SubscriptionPlanFactory {

    private final SubscriptionPlanRepository subscriptionPlanRepository;

    public SubscriptionPlanFactory(SubscriptionPlanRepository subscriptionPlanRepository) {
        this.subscriptionPlanRepository = subscriptionPlanRepository;
    }

    public SubscriptionPlan create(String name, String description,
                                   BigDecimal price, Integer duration) {
        if (subscriptionPlanRepository.existsByName(name)) {
            throw new SubscriptionPlanAlreadyExistsException(name);
        }
        return SubscriptionPlan.create(name, description, price, duration);
    }
}