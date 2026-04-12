package dev.studentpp1.streamingservice.subscription.application.command.subscription;

import dev.studentpp1.streamingservice.subscription.domain.exception.SubscriptionPlanNotFoundException;
import dev.studentpp1.streamingservice.subscription.domain.factory.UserSubscriptionFactory;
import dev.studentpp1.streamingservice.subscription.domain.model.SubscriptionPlan;
import dev.studentpp1.streamingservice.subscription.domain.model.SubscriptionStatus;
import dev.studentpp1.streamingservice.subscription.domain.model.UserSubscription;
import dev.studentpp1.streamingservice.subscription.domain.repository.SubscriptionPlanRepository;
import dev.studentpp1.streamingservice.subscription.domain.repository.UserSubscriptionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

class CreateUserSubscriptionAfterPaymentHandlerTest {

    private SubscriptionPlanRepository subscriptionPlanRepository;
    private UserSubscriptionRepository userSubscriptionRepository;
    private UserSubscriptionFactory userSubscriptionFactory;
    private CreateUserSubscriptionAfterPaymentHandler handler;

    @BeforeEach
    void setUp() {
        subscriptionPlanRepository = mock(SubscriptionPlanRepository.class);
        userSubscriptionRepository = mock(UserSubscriptionRepository.class);
        userSubscriptionFactory = mock(UserSubscriptionFactory.class);
        handler = new CreateUserSubscriptionAfterPaymentHandler(
                subscriptionPlanRepository,
                userSubscriptionRepository,
                userSubscriptionFactory
        );
    }

    @Test
    void handle_planNotFound_throwsSubscriptionPlanNotFoundException() {
        when(subscriptionPlanRepository.findByName("Basic")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> handler.handle("Basic", 1L))
                .isInstanceOf(SubscriptionPlanNotFoundException.class);

        verifyNoInteractions(userSubscriptionFactory);
        verify(userSubscriptionRepository, never()).save(any());
    }

    @Test
    void handle_validCommand_createsAndSavesSubscription() {
        SubscriptionPlan plan = SubscriptionPlan.restore(
                10L, "Basic", "desc", BigDecimal.valueOf(9.99), 30, Set.of(), 0L);
        UserSubscription created = UserSubscription.restore(
                null, 1L, 10L, LocalDateTime.now(), LocalDateTime.now().plusDays(30), SubscriptionStatus.ACTIVE);
        UserSubscription saved = UserSubscription.restore(
                5L, 1L, 10L, created.getStartTime(), created.getEndTime(), SubscriptionStatus.ACTIVE);

        when(subscriptionPlanRepository.findByName("Basic")).thenReturn(Optional.of(plan));
        when(userSubscriptionFactory.create(eq(1L), eq(10L), any(LocalDateTime.class), eq(30))).thenReturn(created);
        when(userSubscriptionRepository.save(created)).thenReturn(saved);

        UserSubscription result = handler.handle("Basic", 1L);

        assertThat(result.getId()).isEqualTo(5L);
        assertThat(result.getUserId()).isEqualTo(1L);
        assertThat(result.getPlanId()).isEqualTo(10L);
        verify(userSubscriptionRepository).save(created);
    }
}

