package dev.studentpp1.streamingservice.notification;

import dev.studentpp1.streamingservice.common.event.EventBus;
import dev.studentpp1.streamingservice.notification.listener.SubscriptionNotificationListener;
import dev.studentpp1.streamingservice.notification.port.SubscriptionNotification;
import dev.studentpp1.streamingservice.subscription.api.event.SubscriptionActivatedEvent;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.LocalDateTime;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class SubscriptionNotificationListenerTest {

    @Test
    void onActivated_sendsActivationNotification() {
        SubscriptionNotification notification = mock(SubscriptionNotification.class);
        SubscriptionNotificationListener listener = new SubscriptionNotificationListener(notification, mock(EventBus.class));
        LocalDateTime expiresAt = LocalDateTime.now().plusDays(30);

        listener.onActivated(new SubscriptionActivatedEvent(
                44L, 2L, "user@test.com", "Premium", expiresAt, Instant.now()
        ));

        verify(notification).notifyActivated("user@test.com", "Premium", expiresAt);
    }
}
