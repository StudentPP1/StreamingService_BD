package dev.studentpp1.streamingservice.payments.domain.repository;

import dev.studentpp1.streamingservice.payments.domain.model.PaymentHistoryItem;
import dev.studentpp1.streamingservice.payments.domain.model.Payment;
import dev.studentpp1.streamingservice.payments.domain.model.PaymentStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface PaymentRepository {
    Payment save(Payment payment);
    Payment saveWithSubscription(Payment payment, Long userSubscriptionId);
    Optional<Payment> findByProviderSessionId(String sessionId);
    Optional<Payment> findByProviderSessionIdForUpdate(String sessionId);
    List<PaymentHistoryItem> getPaymentByUserId(Long userId);
    List<PaymentHistoryItem> getPaymentByUserSubscription(Long userId, Long userSubscriptionId);
    int deletePaymentsBefore(LocalDateTime dateTime);
    int deleteByStatusAndCreatedAtBefore(PaymentStatus status, LocalDateTime threshold);
}