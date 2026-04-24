package dev.studentpp1.streamingservice.payments.application.command.webhook;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.stripe.model.Event;
import dev.studentpp1.streamingservice.common.event.EventBus;
import dev.studentpp1.streamingservice.payments.api.event.PaymentFailedEvent;
import dev.studentpp1.streamingservice.payments.api.event.PaymentSucceededEvent;
import dev.studentpp1.streamingservice.payments.domain.model.Payment;
import dev.studentpp1.streamingservice.payments.domain.model.PaymentStatus;
import dev.studentpp1.streamingservice.payments.domain.port.SubscriptionAfterPaymentPort;
import dev.studentpp1.streamingservice.payments.domain.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentWebhookCommandHandler {

    private static final String EVENT_SESSION_COMPLETED = "checkout.session.completed";
    private static final String EVENT_SESSION_EXPIRED = "checkout.session.expired";
    private static final String EVENT_PAYMENT_INTENT_FAILED = "payment_intent.payment_failed";
    private static final String METADATA_USER_ID = "userId";
    private static final String METADATA_USER_EMAIL = "userEmail";
    private static final String METADATA_PLAN_NAME = "planName";
    private static final String METADATA_PRODUCT_NAME = "productName";

    private final PaymentRepository paymentRepository;
    private final SubscriptionAfterPaymentPort subscriptionPort;
    private final EventBus eventBus;
    private final ObjectMapper objectMapper;

    @Transactional
    public void handle(HandlePaymentWebhookCommand command) {
        handlePaymentEvent(command.event());
    }

    @Transactional
    public void handlePaymentEvent(Event event) {
        String type = event.getType();
        log.info("Received Stripe event type={}", type);
        switch (type) {
            case EVENT_SESSION_COMPLETED -> handleSuccess(event);
            case EVENT_SESSION_EXPIRED -> handleFailed(event);
            case EVENT_PAYMENT_INTENT_FAILED -> handlePaymentIntentFailed(event);
            default -> log.info("Ignored Stripe event type={}", type);
        }
    }

    private void handleSuccess(Event event) {
        SessionPayload payload = parse(event);
        if (payload == null) {
            return;
        }
        Payment payment = paymentRepository
                .findByProviderSessionIdForUpdate(payload.sessionId())
                .orElseThrow(() -> new IllegalStateException("Payment not found"));
        if (payment.getStatus() == PaymentStatus.COMPLETED) {
            return;
        }
        payment.markAsPaid();
        Long subscriptionId = subscriptionPort.onPaymentSucceeded(
                payment.getId(), Long.valueOf(payload.userId()), payload.userEmail(), payload.planName()
        );
        payment.assignSubscription(subscriptionId);
        paymentRepository.save(payment);
        eventBus.publish(new PaymentSucceededEvent(
                payment.getId(), Long.valueOf(payload.userId()), payload.userEmail(),
                payload.planName(), payload.sessionId(),
                payment.getMoney().amount(), payment.getMoney().currency(), Instant.now()
        ));
        log.info("Payment processed successfully for sessionId={}", payload.sessionId());
    }

    private void handleFailed(Event event) {
        SessionPayload payload = parse(event);
        if (payload == null) {
            log.warn("{} skipped: payload is null", EVENT_SESSION_EXPIRED);
            return;
        }
        Payment payment = paymentRepository
                .findByProviderSessionIdForUpdate(payload.sessionId())
                .orElseThrow(() -> new IllegalStateException(
                        "Payment not found for sessionId=" + payload.sessionId()));
        if (payment.getStatus() == PaymentStatus.COMPLETED) {
            log.info("Ignoring FAILED event for COMPLETED payment, sessionId={}", payload.sessionId());
            return;
        }
        if (payment.getStatus() == PaymentStatus.FAILED) {
            log.info("Ignoring duplicate FAILED event, sessionId={}", payload.sessionId());
            return;
        }
        subscriptionPort.onPaymentFailed(
                Long.valueOf(payload.userId()), payload.userEmail(), payload.planName(),
                payment.getUserSubscriptionId(), "Payment was not completed"
        );
        payment.markAsFailed();
        paymentRepository.save(payment);
        eventBus.publish(new PaymentFailedEvent(
                payment.getId(), Long.valueOf(payload.userId()), payload.userEmail(),
                payload.planName(), payload.sessionId(), payment.getUserSubscriptionId(),
                "Payment was not completed", payment.getMoney().amount(),
                payment.getMoney().currency(), Instant.now()
        ));
        log.info("Payment FAILED, sessionId={}, userId={}, plan={}",
                payload.sessionId(), payload.userId(), payload.planName());
    }

    private void handlePaymentIntentFailed(Event event) {
        SessionPayload payload = parse(event);
        if (payload == null) {
            log.warn("{} skipped: payload is null", EVENT_PAYMENT_INTENT_FAILED);
            return;
        }
        Payment payment = paymentRepository
                .findByUserIdAndStatusForUpdate(Long.valueOf(payload.userId()), PaymentStatus.PENDING)
                .orElse(null);
        if (payment == null) {
            log.warn("No PENDING payment found for userId={}, skipping", payload.userId());
            return;
        }
        subscriptionPort.onPaymentFailed(
                Long.valueOf(payload.userId()), payload.userEmail(), payload.planName(),
                payment.getUserSubscriptionId(), "Payment attempt failed"
        );
        log.info("Payment attempt FAILED via payment_intent for userId={}, plan={}",
                payload.userId(), payload.planName());
    }

    private SessionPayload parse(Event event) {
        try {
            String rawJson = event.getDataObjectDeserializer().getRawJson();
            JsonNode root = objectMapper.readTree(rawJson);
            String sessionId = root.path("id").asText(null);
            if (sessionId == null) {
                return null;
            }
            JsonNode metadata = root.path("metadata");
            String userId = metadata.path(METADATA_USER_ID).asText(null);
            String userEmail = metadata.path(METADATA_USER_EMAIL).asText(null);
            String planName = metadata.path(METADATA_PLAN_NAME).asText(null);
            if (planName == null) {
                planName = metadata.path(METADATA_PRODUCT_NAME).asText(null);
            }
            if (userId == null || planName == null) {
                return null;
            }
            return new SessionPayload(sessionId, userId, userEmail, planName);
        } catch (Exception e) {
            log.error("Failed to parse Stripe event payload", e);
            return null;
        }
    }

    @Scheduled(cron = "0 0 3 * * *")
    @Transactional
    public void deleteStalePendingPayments() {
        LocalDateTime threshold = LocalDateTime.now().minusHours(24);
        int deleted = paymentRepository.deleteByStatusAndCreatedAtBefore(
                PaymentStatus.PENDING, threshold);
        log.info("Deleted {} stale PENDING payments older than {}", deleted, threshold);
    }

    record SessionPayload(String sessionId, String userId, String userEmail, String planName) {}
}
