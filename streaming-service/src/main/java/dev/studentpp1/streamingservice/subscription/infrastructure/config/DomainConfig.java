package dev.studentpp1.streamingservice.subscription.infrastructure.config;

import dev.studentpp1.streamingservice.subscription.domain.factory.SubscriptionPlanFactory;
import dev.studentpp1.streamingservice.subscription.domain.factory.UserSubscriptionFactory;
import dev.studentpp1.streamingservice.subscription.domain.repository.SubscriptionPlanRepository;
import dev.studentpp1.streamingservice.subscription.domain.repository.UserSubscriptionRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration("subscriptionDomainConfig")
public class DomainConfig {

    @Bean
    public SubscriptionPlanFactory subscriptionPlanFactory(SubscriptionPlanRepository subscriptionPlanRepository) {
        return new SubscriptionPlanFactory(subscriptionPlanRepository);
    }

    @Bean
    public UserSubscriptionFactory userSubscriptionFactory(UserSubscriptionRepository userSubscriptionRepository) {
        return new UserSubscriptionFactory(userSubscriptionRepository);
    }
}

