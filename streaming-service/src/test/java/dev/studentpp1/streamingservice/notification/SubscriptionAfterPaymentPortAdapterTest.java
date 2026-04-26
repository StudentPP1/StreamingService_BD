package dev.studentpp1.streamingservice.notification;

import dev.studentpp1.streamingservice.common.event.EventBus;
import dev.studentpp1.streamingservice.notification.port.SubscriptionNotification;
import dev.studentpp1.streamingservice.subscription.application.command.CancelSubscriptionAfterPaymentFailureHandler;
import dev.studentpp1.streamingservice.subscription.application.command.CreateUserSubscriptionAfterPaymentHandler;
import dev.studentpp1.streamingservice.subscription.domain.event.SubscriptionActivated;
import dev.studentpp1.streamingservice.subscription.domain.model.SubscriptionStatus;
import dev.studentpp1.streamingservice.subscription.domain.model.UserSubscription;
import dev.studentpp1.streamingservice.subscription.infrastructure.adapter.SubscriptionAfterPaymentPortAdapter;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class SubscriptionAfterPaymentPortAdapterTest {

    @Test
    void onPaymentSucceeded_createsSubscription_andPublishesActivationEvent() {
        CreateUserSubscriptionAfterPaymentHandler createHandler = mock(CreateUserSubscriptionAfterPaymentHandler.class);
        CancelSubscriptionAfterPaymentFailureHandler cancelHandler = mock(CancelSubscriptionAfterPaymentFailureHandler.class);
        EventBus eventBus = mock(EventBus.class);
        SubscriptionNotification notification = mock(SubscriptionNotification.class);
        LocalDateTime expiresAt = LocalDateTime.now().plusDays(30);
        when(createHandler.handle("Premium", 7L)).thenReturn(UserSubscription.restore(
                15L,
                7L,
                3L,
                LocalDateTime.now(),
                expiresAt,
                SubscriptionStatus.ACTIVE
        ));
        SubscriptionAfterPaymentPortAdapter adapter = new SubscriptionAfterPaymentPortAdapter(
                createHandler, cancelHandler, eventBus, notification
        );
        Long result = adapter.onPaymentSucceeded(101L, 7L, "user@test.com", "Premium");
        assertThat(result).isEqualTo(15L);
        verify(createHandler).handle("Premium", 7L);
        verify(eventBus).publish(any(SubscriptionActivated.class));
        verifyNoInteractions(notification);
    }

    @Test
    void onPaymentFailed_withExistingSubscription_cancelsAndNotifiesSynchronously() {
        CreateUserSubscriptionAfterPaymentHandler createHandler = mock(CreateUserSubscriptionAfterPaymentHandler.class);
        CancelSubscriptionAfterPaymentFailureHandler cancelHandler = mock(CancelSubscriptionAfterPaymentFailureHandler.class);
        EventBus eventBus = mock(EventBus.class);
        SubscriptionNotification notification = mock(SubscriptionNotification.class);
        SubscriptionAfterPaymentPortAdapter adapter = new SubscriptionAfterPaymentPortAdapter(
                createHandler, cancelHandler, eventBus, notification
        );
        adapter.onPaymentFailed(7L, "user@test.com", "Premium", 55L, "Payment was not completed");
        verify(cancelHandler).handle(55L);
        verify(notification).notifyFailed("user@test.com", "Premium", "Payment was not completed");
        verifyNoInteractions(eventBus);
    }

    @Test
    void onPaymentFailed_withoutExistingSubscription_notifiesOnly() {
        CreateUserSubscriptionAfterPaymentHandler createHandler = mock(CreateUserSubscriptionAfterPaymentHandler.class);
        CancelSubscriptionAfterPaymentFailureHandler cancelHandler = mock(CancelSubscriptionAfterPaymentFailureHandler.class);
        EventBus eventBus = mock(EventBus.class);
        SubscriptionNotification notification = mock(SubscriptionNotification.class);
        SubscriptionAfterPaymentPortAdapter adapter = new SubscriptionAfterPaymentPortAdapter(
                createHandler, cancelHandler, eventBus, notification
        );
        adapter.onPaymentFailed(7L, "user@test.com", "Premium", null, "Payment was not completed");
        verify(cancelHandler, never()).handle(anyLong());
        verify(notification).notifyFailed("user@test.com", "Premium", "Payment was not completed");
        verifyNoInteractions(eventBus);
    }

    @Test
    void onPaymentFailed_swallowsNotificationError_doesNotPropagateException() {
        CreateUserSubscriptionAfterPaymentHandler createHandler = mock(CreateUserSubscriptionAfterPaymentHandler.class);
        CancelSubscriptionAfterPaymentFailureHandler cancelHandler = mock(CancelSubscriptionAfterPaymentFailureHandler.class);
        EventBus eventBus = mock(EventBus.class);
        SubscriptionNotification notification = mock(SubscriptionNotification.class);
        doThrow(new RuntimeException("notification down"))
                .when(notification).notifyFailed("user@test.com", "Premium", "Payment was not completed");
        SubscriptionAfterPaymentPortAdapter adapter = new SubscriptionAfterPaymentPortAdapter(
                createHandler, cancelHandler, eventBus, notification
        );
        assertThatNoException().isThrownBy(() -> adapter.onPaymentFailed(
                7L, "user@test.com", "Premium", null, "Payment was not completed"
        ));
        verify(notification).notifyFailed("user@test.com", "Premium", "Payment was not completed");
    }
}
