package dev.studentpp1.streamingservice.subscription.application.command;

import dev.studentpp1.streamingservice.payments.api.event.PaymentFailedEvent;
import dev.studentpp1.streamingservice.payments.api.event.PaymentSucceededEvent;
import dev.studentpp1.streamingservice.subscription.application.event.handler.CancelSubscriptionAfterPaymentFailureHandler;
import dev.studentpp1.streamingservice.subscription.application.event.handler.CreateUserSubscriptionAfterPaymentHandler;
import dev.studentpp1.streamingservice.subscription.application.event.listener.SubscriptionPaymentListener;
import dev.studentpp1.streamingservice.subscription.api.event.SubscriptionActivatedEvent;
import dev.studentpp1.streamingservice.subscription.api.event.SubscriptionFailedEvent;
import dev.studentpp1.streamingservice.subscription.api.event.SubscriptionLinkedToPaymentEvent;
import dev.studentpp1.streamingservice.subscription.domain.model.SubscriptionStatus;
import dev.studentpp1.streamingservice.subscription.domain.model.UserSubscription;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.context.ApplicationEventPublisher;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class SubscriptionPaymentListenerTest {

    @Test
    void onPaymentSucceeded_createsSubscriptionAndPublishesActivated() {
        CreateUserSubscriptionAfterPaymentHandler createHandler = mock(CreateUserSubscriptionAfterPaymentHandler.class);
        CancelSubscriptionAfterPaymentFailureHandler cancelHandler = mock(CancelSubscriptionAfterPaymentFailureHandler.class);
        ApplicationEventPublisher publisher = mock(ApplicationEventPublisher.class);

        SubscriptionPaymentListener handler = new SubscriptionPaymentListener(createHandler, cancelHandler, publisher);

        UserSubscription created = UserSubscription.restore(
                44L,
                2L,
                10L,
                LocalDateTime.now(),
                LocalDateTime.now().plusDays(30),
                SubscriptionStatus.ACTIVE
        );
        when(createHandler.handle("Premium", 2L)).thenReturn(created);

        PaymentSucceededEvent event = new PaymentSucceededEvent(
                1L, 2L, "user@test.com", "Premium", "sess_1", java.math.BigDecimal.TEN, "USD", Instant.now());

        handler.onPaymentSucceeded(event);

        ArgumentCaptor<Object> eventsCaptor = ArgumentCaptor.forClass(Object.class);
        verify(publisher, times(2)).publishEvent(eventsCaptor.capture());
        List<Object> publishedEvents = eventsCaptor.getAllValues();

        SubscriptionLinkedToPaymentEvent linkEvent = (SubscriptionLinkedToPaymentEvent) publishedEvents.stream()
                .filter(SubscriptionLinkedToPaymentEvent.class::isInstance)
                .findFirst()
                .orElseThrow();
        assertThat(linkEvent.paymentId()).isEqualTo(1L);
        assertThat(linkEvent.providerSessionId()).isEqualTo("sess_1");
        assertThat(linkEvent.subscriptionId()).isEqualTo(44L);

        ArgumentCaptor<SubscriptionActivatedEvent> captor = ArgumentCaptor.forClass(SubscriptionActivatedEvent.class);
        verify(publisher).publishEvent(captor.capture());
        assertThat(captor.getValue().subscriptionId()).isEqualTo(44L);
    }

    @Test
    void onPaymentFailed_cancelsExistingSubscriptionAndPublishesFailed() {
        CreateUserSubscriptionAfterPaymentHandler createHandler = mock(CreateUserSubscriptionAfterPaymentHandler.class);
        CancelSubscriptionAfterPaymentFailureHandler cancelHandler = mock(CancelSubscriptionAfterPaymentFailureHandler.class);
        ApplicationEventPublisher publisher = mock(ApplicationEventPublisher.class);

        SubscriptionPaymentListener handler = new SubscriptionPaymentListener(createHandler, cancelHandler, publisher);

        PaymentFailedEvent event = new PaymentFailedEvent(
                1L, 2L, "user@test.com", "Premium", "sess_1", 99L, "expired", java.math.BigDecimal.TEN, "USD", Instant.now());

        handler.onPaymentFailed(event);

        verify(cancelHandler).handle(99L);
        verify(publisher).publishEvent(any(SubscriptionFailedEvent.class));
    }
}

