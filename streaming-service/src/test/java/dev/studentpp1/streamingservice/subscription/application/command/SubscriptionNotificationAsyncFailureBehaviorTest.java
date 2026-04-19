package dev.studentpp1.streamingservice.subscription.application.command;

import dev.studentpp1.streamingservice.subscription.application.event.listener.SubscriptionNotificationListener;
import dev.studentpp1.streamingservice.subscription.domain.event.SubscriptionFailed;
import dev.studentpp1.streamingservice.subscription.domain.port.SubscriptionNotification;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThatCode;

@SpringJUnitConfig(classes = SubscriptionNotificationAsyncFailureBehaviorTest.TestConfig.class)
class SubscriptionNotificationAsyncFailureBehaviorTest {

    @Configuration
    @EnableAsync
    static class TestConfig {
        @Bean
        SubscriptionNotification notificationPort() {
            return new SubscriptionNotification() {
                @Override
                public void notifyActivated(String email, String planName, java.time.LocalDateTime expiresAt) {
                    throw new RuntimeException("async activated notification failure");
                }

                @Override
                public void notifyFailed(String email, String planName, String reason) {
                    throw new RuntimeException("async failed notification failure");
                }
            };
        }

        @Bean
        SubscriptionNotificationListener subscriptionNotificationListener(SubscriptionNotification notificationPort) {
            return new SubscriptionNotificationListener(notificationPort);
        }
    }

    @Autowired
    private ApplicationEventPublisher publisher;

    @Test
    void asyncListenerFailure_doesNotBreakPublisherThread() {
        assertThatCode(() -> publisher.publishEvent(new SubscriptionFailed(
                1L,
                "user@test.com",
                "Premium",
                "payment failed",
                Instant.now()
        ))).doesNotThrowAnyException();
    }
}

