package dev.studentpp1.streamingservice.payments.domain.repository;

import dev.studentpp1.streamingservice.payments.domain.model.Payment;
import dev.studentpp1.streamingservice.payments.domain.model.PaymentStatus;

import java.time.LocalDateTime;
import java.util.Optional;

public interface PaymentRepository {
    Payment save(Payment payment);
    Payment saveWithSubscription(Payment payment, Long userSubscriptionId);
    Optional<Payment> findByProviderSessionId(String sessionId);
    Optional<Payment> findByProviderSessionIdForUpdate(String sessionId);
    int deletePaymentsBefore(LocalDateTime dateTime);
    int deleteByStatusAndCreatedAtBefore(PaymentStatus status, LocalDateTime threshold);
}