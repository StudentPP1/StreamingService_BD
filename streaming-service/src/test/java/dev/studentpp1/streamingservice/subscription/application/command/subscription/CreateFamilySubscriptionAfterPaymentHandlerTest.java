package dev.studentpp1.streamingservice.subscription.application.command.subscription;

import dev.studentpp1.streamingservice.subscription.domain.exception.InvalidFamilyMemberException;
import dev.studentpp1.streamingservice.subscription.domain.exception.SubscriptionPlanNotFoundException;
import dev.studentpp1.streamingservice.subscription.domain.model.SubscriberContext;
import dev.studentpp1.streamingservice.subscription.domain.model.SubscriptionPlan;
import dev.studentpp1.streamingservice.subscription.domain.model.UserSubscription;
import dev.studentpp1.streamingservice.subscription.domain.port.SubscriberProvider;
import dev.studentpp1.streamingservice.subscription.domain.repository.SubscriptionPlanRepository;
import dev.studentpp1.streamingservice.subscription.domain.repository.UserSubscriptionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

class CreateFamilySubscriptionAfterPaymentHandlerTest {

    private SubscriptionPlanRepository subscriptionPlanRepository;
    private UserSubscriptionRepository userSubscriptionRepository;
    private SubscriberProvider subscriberProvider;
    private CreateFamilySubscriptionAfterPaymentHandler handler;

    @BeforeEach
    void setUp() {
        subscriptionPlanRepository = mock(SubscriptionPlanRepository.class);
        userSubscriptionRepository = mock(UserSubscriptionRepository.class);
        subscriberProvider = mock(SubscriberProvider.class);
        handler = new CreateFamilySubscriptionAfterPaymentHandler(
                subscriptionPlanRepository,
                userSubscriptionRepository,
                subscriberProvider
        );
    }

    @Test
    void handle_planNotFound_throwsSubscriptionPlanNotFoundException() {
        when(subscriptionPlanRepository.findByName("Family")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> handler.handle(1L, "Family", List.of("member@mail.com")))
                .isInstanceOf(SubscriptionPlanNotFoundException.class);

        verify(userSubscriptionRepository, never()).saveAll(anyList());
    }

    @Test
    void handle_mainUserInFamily_throwsInvalidFamilyMemberException() {
        SubscriptionPlan plan = SubscriptionPlan.restore(
                10L, "Family", "desc", BigDecimal.valueOf(19.99), 30, Set.of(), 0L);
        when(subscriptionPlanRepository.findByName("Family")).thenReturn(Optional.of(plan));
        when(subscriberProvider.getById(1L)).thenReturn(new SubscriberContext(1L, "main@mail.com"));
        when(subscriberProvider.getByEmail("main@mail.com")).thenReturn(new SubscriberContext(1L, "main@mail.com"));

        assertThatThrownBy(() -> handler.handle(1L, "Family", List.of("main@mail.com")))
                .isInstanceOf(InvalidFamilyMemberException.class);

        verify(userSubscriptionRepository, never()).saveAll(anyList());
    }

    @Test
    void handle_validCommand_createsSubscriptionsForMainAndMembers() {
        SubscriptionPlan plan = SubscriptionPlan.restore(
                10L, "Family", "desc", BigDecimal.valueOf(19.99), 30, Set.of(), 0L);
        when(subscriptionPlanRepository.findByName("Family")).thenReturn(Optional.of(plan));
        when(subscriberProvider.getById(1L)).thenReturn(new SubscriberContext(1L, "main@mail.com"));
        when(subscriberProvider.getByEmail("member@mail.com")).thenReturn(new SubscriberContext(2L, "member@mail.com"));

        when(userSubscriptionRepository.saveAll(anyList())).thenAnswer(invocation -> invocation.getArgument(0));

        List<UserSubscription> result = handler.handle(1L, "Family", List.of("member@mail.com"));

        assertThat(result).hasSize(2);
        assertThat(result).extracting(UserSubscription::getUserId).containsExactlyInAnyOrder(1L, 2L);
        assertThat(result).allMatch(s -> s.getPlanId().equals(10L));
        verify(userSubscriptionRepository).saveAll(anyList());
    }
}

