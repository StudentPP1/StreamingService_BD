package dev.studentpp1.streamingservice.notification.adapter;

import dev.studentpp1.streamingservice.notification.port.SubscriptionNotification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

import static jakarta.mail.Message.RecipientType.TO;

@Slf4j
@Component
@RequiredArgsConstructor
public class SubscriptionNotificationAdapter implements SubscriptionNotification {

    private final JavaMailSender javaMailSender;

    @Override
    public void notifyActivated(String email, String planName, LocalDateTime expiresAt) {
        log.info("Subscription activated: email={}, plan={}, expires={}", email, planName, expiresAt);
        javaMailSender.send(mimeMessage -> {
            mimeMessage.setFrom("no-reply@example.com");
            mimeMessage.setRecipients(TO, email);
            mimeMessage.setSubject("Your subscription is activated!");
            mimeMessage.setText(String.format(
                    "Congratulations! Your subscription to the %s plan is now active and will expire on %s.",
                    planName, expiresAt));
        });
    }

    @Override
    public void notifyFailed(String email, String planName, String reason) {
        log.warn("Subscription failed: email={}, plan={}, reason={}", email, planName, reason);
        javaMailSender.send(mimeMessage -> {
            mimeMessage.setFrom("no-reply@example.com");
            mimeMessage.setRecipients(TO, email);
            mimeMessage.setSubject("Your subscription activation failed");
            mimeMessage.setText(String.format(
                    "Unfortunately, your subscription to the %s plan could not be activated. Reason: %s.",
                    planName, reason));
        });
    }
}
