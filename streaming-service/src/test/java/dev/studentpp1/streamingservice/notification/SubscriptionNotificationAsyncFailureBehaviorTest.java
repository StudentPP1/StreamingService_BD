package dev.studentpp1.streamingservice.notification;

import dev.studentpp1.streamingservice.common.event.EventBus;
import dev.studentpp1.streamingservice.notification.listener.SubscriptionNotificationListener;
import dev.studentpp1.streamingservice.notification.port.SubscriptionNotification;
import dev.studentpp1.streamingservice.subscription.domain.event.SubscriptionActivated;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.util.StopWatch;

import java.time.Instant;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

@SpringJUnitConfig(classes = SubscriptionNotificationAsyncFailureBehaviorTest.TestConfig.class)
class SubscriptionNotificationAsyncFailureBehaviorTest {

    @Configuration
    static class TestConfig {
        @Bean
        SubscriptionNotification notificationPort() {
            return new SubscriptionNotification() {
                @Override
                public void notifyActivated(String email, String planName, LocalDateTime expiresAt) {
                    throw new RuntimeException("async notification failure");
                }
                @Override
                public void notifyFailed(String email, String planName, String reason) {}
            };
        }

        @Bean
        EventBus eventBus() {
            return new EventBus();
        }

        @Bean
        SubscriptionNotificationListener asyncSubscriptionNotificationListener(
                SubscriptionNotification notificationPort, EventBus eventBus) {
            return new SubscriptionNotificationListener(notificationPort, eventBus);
        }
    }

    @Autowired
    private EventBus eventBus;

    @Test
    void asyncActivatedListenerFailure_doesNotBreakPublisherThread() {
        assertThatCode(() -> eventBus.publish(new SubscriptionActivated(
                1L, 2L, "user@test.com", "Premium", LocalDateTime.now().plusDays(30), Instant.now()
        ))).doesNotThrowAnyException();
    }

    @Test
    void asyncActivatedPublish_returnsImmediately() {
        StopWatch sw = new StopWatch();
        sw.start();
        eventBus.publish(new SubscriptionActivated(
                1L, 2L, "user@test.com", "Premium", LocalDateTime.now().plusDays(30), Instant.now()
        ));
        sw.stop();
        assertThat(sw.getTotalTimeMillis()).isLessThan(200);
    }
}
