package dev.studentpp1.streamingservice.subscription.application.command;

import dev.studentpp1.streamingservice.subscription.application.event.listener.SubscriptionNotificationListener;
import dev.studentpp1.streamingservice.subscription.domain.event.SubscriptionActivated;
import dev.studentpp1.streamingservice.subscription.domain.event.SubscriptionFailed;
import dev.studentpp1.streamingservice.subscription.domain.port.SubscriptionNotification;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.LocalDateTime;

import static org.mockito.Mockito.*;

class SubscriptionNotificationAdapterListenerTest {

    @Test
    void onActivated_sendsActivationNotification() {
        SubscriptionNotification notification = mock(SubscriptionNotification.class);
        SubscriptionNotificationListener handler = new SubscriptionNotificationListener(notification);

        handler.onActivated(new SubscriptionActivated(
                1L,
                2L,
                "user@test.com",
                "Premium",
                LocalDateTime.now().plusDays(30),
                Instant.now()
        ));

        verify(notification).notifyActivated(eq("user@test.com"), eq("Premium"), any(LocalDateTime.class));
    }

    @Test
    void onFailed_sendsFailureNotification() {
        SubscriptionNotification notification = mock(SubscriptionNotification.class);
        SubscriptionNotificationListener handler = new SubscriptionNotificationListener(notification);

        handler.onFailed(new SubscriptionFailed(
                2L,
                "user@test.com",
                "Premium",
                "Payment failed",
                Instant.now()
        ));

        verify(notification).notifyFailed("user@test.com", "Premium", "Payment failed");
    }
}

