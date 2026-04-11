package dev.studentpp1.streamingservice.subscription.application;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.studentpp1.streamingservice.subscription.application.usecase.SubscriptionService;
import dev.studentpp1.streamingservice.subscription.domain.exception.ActiveSubscriptionAlreadyExistsException;
import dev.studentpp1.streamingservice.subscription.domain.exception.SubscriptionAccessDeniedException;
import dev.studentpp1.streamingservice.subscription.domain.exception.SubscriptionNotFoundException;
import dev.studentpp1.streamingservice.subscription.domain.exception.SubscriptionPlanNotFoundException;
import dev.studentpp1.streamingservice.subscription.domain.factory.UserSubscriptionFactory;
import dev.studentpp1.streamingservice.subscription.domain.model.SubscriptionPlan;
import dev.studentpp1.streamingservice.subscription.domain.model.SubscriptionStatus;
import dev.studentpp1.streamingservice.subscription.domain.model.UserSubscription;
import dev.studentpp1.streamingservice.subscription.domain.port.SubscriberProvider;
import dev.studentpp1.streamingservice.subscription.domain.port.SubscriptionPaymentGateway;
import dev.studentpp1.streamingservice.subscription.domain.repository.SubscriptionPlanRepository;
import dev.studentpp1.streamingservice.subscription.domain.repository.UserSubscriptionRepository;
import dev.studentpp1.streamingservice.subscription.presentation.dto.request.SubscribeRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class SubscriptionServiceTest {

    private SubscriptionPlanRepository planRepository;
    private UserSubscriptionRepository subscriptionRepository;
    private SubscriberProvider subscriberProvider;
    private SubscriptionPaymentGateway paymentGateway;
    private UserSubscriptionFactory subscriptionFactory;
    private SubscriptionService subscriptionService;

    @BeforeEach
    void setUp() {
        planRepository = mock(SubscriptionPlanRepository.class);
        subscriptionRepository = mock(UserSubscriptionRepository.class);
        subscriberProvider = mock(SubscriberProvider.class);
        paymentGateway = mock(SubscriptionPaymentGateway.class);
        subscriptionFactory = mock(UserSubscriptionFactory.class);
        subscriptionService = new SubscriptionService(
                planRepository, subscriptionRepository, subscriberProvider,
                paymentGateway, subscriptionFactory, new ObjectMapper());
    }

    @Test
    void subscribeUser_planNotFound_throwsSubscriptionPlanNotFoundException() {
        when(planRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                subscriptionService.subscribeUser(new SubscribeRequest(99L), 1L))
                .isInstanceOf(SubscriptionPlanNotFoundException.class);
    }

    @Test
    void subscribeUser_alreadyHasActivePlan_throwsActiveSubscriptionAlreadyExistsException() {
        SubscriptionPlan plan = SubscriptionPlan.restore(
                10L, "Basic", "desc", BigDecimal.valueOf(9.99), 30, Set.of(), 0L);

        when(planRepository.findById(10L)).thenReturn(Optional.of(plan));
        when(subscriptionRepository.existsByUserIdInAndPlanIdAndStatus(
                List.of(1L), 10L, SubscriptionStatus.ACTIVE))
                .thenReturn(true);

        assertThatThrownBy(() ->
                subscriptionService.subscribeUser(new SubscribeRequest(10L), 1L))
                .isInstanceOf(ActiveSubscriptionAlreadyExistsException.class);
    }

    @Test
    void cancelSubscription_notFound_throwsSubscriptionNotFoundException() {
        when(subscriptionRepository.findByIdWithLock(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> subscriptionService.cancelSubscription(99L, 1L))
                .isInstanceOf(SubscriptionNotFoundException.class);
    }

    @Test
    void cancelSubscription_wrongUser_throwsSubscriptionAccessDeniedException() {
        UserSubscription sub = UserSubscription.restore(
                1L, 2L, 10L,
                LocalDateTime.now(), LocalDateTime.now().plusDays(30),
                SubscriptionStatus.ACTIVE);

        when(subscriptionRepository.findByIdWithLock(1L)).thenReturn(Optional.of(sub));

        assertThatThrownBy(() -> subscriptionService.cancelSubscription(1L, 99L))
                .isInstanceOf(SubscriptionAccessDeniedException.class);
    }

    @Test
    void cancelSubscription_validOwner_cancelsAndSaves() {
        UserSubscription sub = UserSubscription.restore(
                1L, 5L, 10L,
                LocalDateTime.now(), LocalDateTime.now().plusDays(30),
                SubscriptionStatus.ACTIVE);

        when(subscriptionRepository.findByIdWithLock(1L)).thenReturn(Optional.of(sub));

        subscriptionService.cancelSubscription(1L, 5L);

        assertThat(sub.getStatus()).isEqualTo(SubscriptionStatus.CANCELLED);
        verify(subscriptionRepository).save(sub);
    }
}