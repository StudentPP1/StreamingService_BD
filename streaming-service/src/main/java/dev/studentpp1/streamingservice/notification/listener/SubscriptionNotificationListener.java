package dev.studentpp1.streamingservice.notification.listener;

import dev.studentpp1.streamingservice.common.event.EventBus;
import dev.studentpp1.streamingservice.notification.port.SubscriptionNotification;
import dev.studentpp1.streamingservice.subscription.api.event.SubscriptionActivatedEvent;
import dev.studentpp1.streamingservice.subscription.api.event.SubscriptionFailedEvent;
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
        eventBus.subscribeAsync(SubscriptionActivatedEvent.class, this::onActivated);
        eventBus.subscribeAsync(SubscriptionFailedEvent.class, this::onFailed);
    }

    public void onActivated(SubscriptionActivatedEvent event) {
        notificationService.notifyActivated(event.userEmail(), event.planName(), event.expiresAt());
    }

    public void onFailed(SubscriptionFailedEvent event) {
        notificationService.notifyFailed(event.userEmail(), event.planName(), event.reason());
    }
}
