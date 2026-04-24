package dev.studentpp1.streamingservice.notification.port;

import java.time.LocalDateTime;

public interface SubscriptionNotification {
    void notifyActivated(String email, String planName, LocalDateTime expiresAt);
    void notifyFailed(String email, String planName, String reason);
}
