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
        log.info("Subscription activated: email={}, plan={}, expires={}",
                email, planName, expiresAt);
        sendEmail(email, "Your subscription is activated!", "Congratulations! Your subscription to the %s plan is now active and will expire on %s.", planName, expiresAt.toString());
    }

    @Override
    public void notifyFailed(String email, String planName, String reason) {
        log.warn("Subscription failed: email={}, plan={}, reason={}",
                email, planName, reason);
        sendEmail(email, "Your subscription activation failed", "Unfortunately, your subscription to the %s plan could not be activated. Reason: %s. Please try again or contact support.", planName, reason);
    }

    private void sendEmail(String email, String title, String content, String planName, String reason) {
        javaMailSender.send(mimeMessage -> {
            mimeMessage.setFrom("no-reply@example.com");
            mimeMessage.setRecipients(TO, email);
            mimeMessage.setSubject(title);
            mimeMessage.setText(String.format(content, planName, reason));
        });
    }
}
