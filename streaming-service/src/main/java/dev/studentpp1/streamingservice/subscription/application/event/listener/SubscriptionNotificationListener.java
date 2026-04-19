package dev.studentpp1.streamingservice.subscription.application.event.listener;

import dev.studentpp1.streamingservice.subscription.domain.event.SubscriptionActivated;
import dev.studentpp1.streamingservice.subscription.domain.event.SubscriptionFailed;
import dev.studentpp1.streamingservice.subscription.domain.port.SubscriptionNotification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class SubscriptionNotificationListener {

    private final SubscriptionNotification notificationService;

    @Async
    @EventListener
    public void onActivated(SubscriptionActivated event) {
        log.debug("Async notification: subscriptionId={}", event.subscriptionId());
        notificationService.notifyActivated(event.userEmail(), event.planName(), event.expiresAt());
    }

    @Async
    @EventListener
    public void onFailed(SubscriptionFailed event) {
        log.debug("Async notification: userId={}", event.userId());
        notificationService.notifyFailed(event.userEmail(), event.planName(), event.reason());
    }
}
