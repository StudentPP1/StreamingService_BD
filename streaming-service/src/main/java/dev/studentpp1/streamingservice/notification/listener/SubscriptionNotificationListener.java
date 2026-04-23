package dev.studentpp1.streamingservice.notification.listener;

import dev.studentpp1.streamingservice.common.event.EventBus;
import dev.studentpp1.streamingservice.notification.port.SubscriptionNotification;
import dev.studentpp1.streamingservice.subscription.domain.event.SubscriptionActivated;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class SubscriptionNotificationListener {

    private final SubscriptionNotification notificationService;
    private final EventBus eventBus;

    @PostConstruct
    void registerSubscriptions() {
        eventBus.subscribeAsync(SubscriptionActivated.class, this::onActivated);
    }

    public void onActivated(SubscriptionActivated event) {
        notificationService.notifyActivated(event.userEmail(), event.planName(), event.expiresAt());
    }
}
