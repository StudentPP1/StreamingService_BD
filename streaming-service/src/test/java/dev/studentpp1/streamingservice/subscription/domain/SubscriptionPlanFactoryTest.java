package dev.studentpp1.streamingservice.subscription.domain;

import dev.studentpp1.streamingservice.subscription.domain.exception.SubscriptionPlanAlreadyExistsException;
import dev.studentpp1.streamingservice.subscription.domain.factory.SubscriptionPlanFactory;
import dev.studentpp1.streamingservice.subscription.domain.model.SubscriptionPlan;
import dev.studentpp1.streamingservice.subscription.domain.repository.SubscriptionPlanRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class SubscriptionPlanFactoryTest {

    private SubscriptionPlanRepository subscriptionPlanRepository;
    private SubscriptionPlanFactory factory;

    @BeforeEach
    void setUp() {
        subscriptionPlanRepository = mock(SubscriptionPlanRepository.class);
        factory = new SubscriptionPlanFactory(subscriptionPlanRepository);
    }

    @Test
    void create_validPlan_success() {
        when(subscriptionPlanRepository.existsByName("Basic")).thenReturn(false);

        SubscriptionPlan plan = factory.create("Basic", "Basic plan", BigDecimal.valueOf(9.99), 30);

        assertThat(plan.getName()).isEqualTo("Basic");
        assertThat(plan.getId()).isNull();
        verify(subscriptionPlanRepository).existsByName("Basic");
    }

    @Test
    void create_duplicateName_throwsAlreadyExistsException() {
        when(subscriptionPlanRepository.existsByName("Basic")).thenReturn(true);

        assertThatThrownBy(() ->
                factory.create("Basic", "Basic plan", BigDecimal.valueOf(9.99), 30))
                .isInstanceOf(SubscriptionPlanAlreadyExistsException.class);
    }
}