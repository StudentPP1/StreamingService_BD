package dev.studentpp1.streamingservice.payments.application.usecase;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.stripe.model.Event;
import dev.studentpp1.streamingservice.payments.domain.model.Payment;
import dev.studentpp1.streamingservice.payments.domain.model.PaymentStatus;
import dev.studentpp1.streamingservice.payments.domain.port.PaymentCompletionHandler;
import dev.studentpp1.streamingservice.payments.domain.repository.PaymentRepository;
import dev.studentpp1.streamingservice.subscription.domain.model.UserSubscription;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentWebhookService {

    private static final String EVENT_SESSION_COMPLETED = "checkout.session.completed";
    private static final String EVENT_SESSION_EXPIRED = "checkout.session.expired";
    private static final String METADATA_USER_ID = "userId";
    private static final String METADATA_PLAN_NAME = "planName";
    private static final String METADATA_PRODUCT_NAME = "productName";
    private static final String METADATA_FAMILY_MEMBER_EMAILS = "familyMemberEmails";

    private final PaymentRepository paymentRepository;
    private final PaymentCompletionHandler paymentCompletionHandler;
    private final ObjectMapper objectMapper;

    @Transactional
    public void handlePaymentEvent(Event event) {
        String type = event.getType();
        log.info("Received Stripe event type={}", type);
        switch (type) {
            case EVENT_SESSION_COMPLETED -> handleSuccess(event);
            case EVENT_SESSION_EXPIRED -> handleFailed(event);
            default -> log.info("Ignored Stripe event type={}", type);
        }
    }

    private void handleSuccess(Event event) {
        SessionPayload payload = parse(event);
        if (payload == null) return;
        Payment payment = paymentRepository
                .findByProviderSessionIdForUpdate(payload.sessionId())
                .orElseThrow(() -> new IllegalStateException("Payment not found"));
        if (payment.getStatus() == PaymentStatus.COMPLETED) return;
        Long subscriptionId = paymentCompletionHandler.handleSuccess(
                Long.valueOf(payload.userId()),
                payload.planName(),
                payload.familyMemberEmails()
        );
        payment.markAsPaid();
        paymentRepository.saveWithSubscription(payment, subscriptionId);
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
            log.info("Ignoring FAILED event for COMPLETED payment, sessionId={}",
                    payload.sessionId());
            return;
        }
        payment.markAsFailed();
        paymentRepository.save(payment);
        log.info("Payment FAILED, sessionId={}, userId={}, plan={}",
                payload.sessionId(), payload.userId(), payload.planName());
    }

    private SessionPayload parse(Event event) {
        try {
            String rawJson = event.getDataObjectDeserializer().getRawJson();
            JsonNode root = objectMapper.readTree(rawJson);

            String sessionId = root.path("id").asText(null);
            if (sessionId == null) return null;

            JsonNode metadata = root.path("metadata");
            String userId = metadata.path(METADATA_USER_ID).asText(null);
            String planName = metadata.path(METADATA_PLAN_NAME).asText(null);
            if (planName == null) {
                // Backward/forward compatibility between payment and subscription metadata names.
                planName = metadata.path(METADATA_PRODUCT_NAME).asText(null);
            }

            if (userId == null || planName == null) return null;

            String familyMemberEmailsJson = metadata.path(METADATA_FAMILY_MEMBER_EMAILS).asText(null);
            List<String> familyMemberEmails = null;

            if (familyMemberEmailsJson != null) {
                try {
                    familyMemberEmails = objectMapper.readValue(familyMemberEmailsJson, new TypeReference<>() {});
                } catch (Exception e) {
                    log.error("Failed to parse family member emails from metadata", e);
                }
            }

            return new SessionPayload(sessionId, userId, planName, familyMemberEmails);
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

    record SessionPayload(String sessionId, String userId,
                          String planName, List<String> familyMemberEmails) {
    }
}