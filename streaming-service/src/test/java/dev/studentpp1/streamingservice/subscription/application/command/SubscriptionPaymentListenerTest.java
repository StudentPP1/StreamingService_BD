package dev.studentpp1.streamingservice.subscription.application.command;

import dev.studentpp1.streamingservice.payments.domain.event.PaymentFailed;
import dev.studentpp1.streamingservice.payments.domain.event.PaymentSucceeded;
import dev.studentpp1.streamingservice.subscription.application.event.handler.CancelSubscriptionAfterPaymentFailureHandler;
import dev.studentpp1.streamingservice.subscription.application.event.handler.CreateUserSubscriptionAfterPaymentHandler;
import dev.studentpp1.streamingservice.subscription.application.event.listener.SubscriptionPaymentListener;
import dev.studentpp1.streamingservice.subscription.domain.event.SubscriptionActivated;
import dev.studentpp1.streamingservice.subscription.domain.event.SubscriptionFailed;
import dev.studentpp1.streamingservice.subscription.domain.event.SubscriptionLinkedToPayment;
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

        PaymentSucceeded event = new PaymentSucceeded(1L, 2L, "user@test.com", "Premium", "sess_1", Instant.now());

        handler.onPaymentSucceeded(event);

        ArgumentCaptor<Object> eventsCaptor = ArgumentCaptor.forClass(Object.class);
        verify(publisher, times(2)).publishEvent(eventsCaptor.capture());
        List<Object> publishedEvents = eventsCaptor.getAllValues();

        SubscriptionLinkedToPayment linkEvent = (SubscriptionLinkedToPayment) publishedEvents.stream()
                .filter(SubscriptionLinkedToPayment.class::isInstance)
                .findFirst()
                .orElseThrow();
        assertThat(linkEvent.paymentId()).isEqualTo(1L);
        assertThat(linkEvent.providerSessionId()).isEqualTo("sess_1");
        assertThat(linkEvent.subscriptionId()).isEqualTo(44L);

        ArgumentCaptor<SubscriptionActivated> captor = ArgumentCaptor.forClass(SubscriptionActivated.class);
        verify(publisher).publishEvent(captor.capture());
        assertThat(captor.getValue().subscriptionId()).isEqualTo(44L);
    }

    @Test
    void onPaymentFailed_cancelsExistingSubscriptionAndPublishesFailed() {
        CreateUserSubscriptionAfterPaymentHandler createHandler = mock(CreateUserSubscriptionAfterPaymentHandler.class);
        CancelSubscriptionAfterPaymentFailureHandler cancelHandler = mock(CancelSubscriptionAfterPaymentFailureHandler.class);
        ApplicationEventPublisher publisher = mock(ApplicationEventPublisher.class);

        SubscriptionPaymentListener handler = new SubscriptionPaymentListener(createHandler, cancelHandler, publisher);

        PaymentFailed event = new PaymentFailed(1L, 2L, "user@test.com", "Premium", "sess_1", 99L, "expired");

        handler.onPaymentFailed(event);

        verify(cancelHandler).handle(99L);
        verify(publisher).publishEvent(any(SubscriptionFailed.class));
    }
}

