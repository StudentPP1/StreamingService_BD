package dev.studentpp1.streamingservice.subscription.infrastructure.adapter;

import dev.studentpp1.streamingservice.subscription.domain.port.SubscriptionNotification;
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
        log.info("[NOTIFICATION] Subscription activated: email={}, plan={}, expires={}",
                email, planName, expiresAt);
        javaMailSender.send(mimeMessage -> {
            mimeMessage.setFrom("no-reply@example.com");
            mimeMessage.setRecipients(TO, email);
            mimeMessage.setSubject("Your subscription is activated!");
            mimeMessage.setText(String.format("Congratulations! Your subscription to the %s plan is now active and will expire on %s.",
                    planName, expiresAt.toString()));
        });
    }

    @Override
    public void notifyFailed(String email, String planName, String reason) {
        log.warn("[NOTIFICATION] Subscription failed: email={}, plan={}, reason={}",
                email, planName, reason);
        javaMailSender.send(mimeMessage -> {
            mimeMessage.setFrom("no-reply@example.com");
            mimeMessage.setRecipients(TO, email);
            mimeMessage.setSubject("Your subscription activation failed");
            mimeMessage.setText(String.format("Unfortunately, your subscription to the %s plan could not be activated. Reason: %s. Please try again or contact support.",
                    planName, reason));
        });
    }
}
