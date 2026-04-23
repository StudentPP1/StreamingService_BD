package dev.studentpp1.streamingservice.payments.domain.repository;

import dev.studentpp1.streamingservice.payments.domain.model.Payment;
import dev.studentpp1.streamingservice.payments.domain.model.PaymentStatus;

import java.time.LocalDateTime;
import java.util.Optional;

public interface PaymentRepository {
    Payment save(Payment payment);
    Optional<Payment> findByProviderSessionId(String sessionId);
    Optional<Payment> findByProviderSessionIdForUpdate(String sessionId);
    Optional<Payment> findByUserIdAndStatusForUpdate(Long userId, PaymentStatus status);
    int deletePaymentsBefore(LocalDateTime dateTime);
    int deleteByStatusAndCreatedAtBefore(PaymentStatus status, LocalDateTime threshold);
}