package dev.studentpp1.streamingservice.payments.infrastructure.adapter;

import dev.studentpp1.streamingservice.payments.domain.model.PaymentHistoryItem;
import dev.studentpp1.streamingservice.payments.domain.model.Payment;
import dev.studentpp1.streamingservice.payments.domain.model.PaymentStatus;
import dev.studentpp1.streamingservice.payments.domain.repository.PaymentRepository;
import dev.studentpp1.streamingservice.payments.infrastructure.mapper.PaymentPersistenceMapper;
import dev.studentpp1.streamingservice.payments.infrastructure.repository.PaymentJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class PaymentRepositoryAdapter implements PaymentRepository {

    private final PaymentJpaRepository jpaRepository;
    private final PaymentPersistenceMapper mapper;

    @Override
    public Payment save(Payment payment) {
        return mapper.toDomain(jpaRepository.save(mapper.toEntity(payment)));
    }

    @Override
    public Payment saveWithSubscription(Payment payment, Long userSubscriptionId) {
        var entity = mapper.toEntity(payment);
        entity.setUserSubscriptionId(userSubscriptionId);
        return mapper.toDomain(jpaRepository.save(entity));
    }

    @Override
    public Optional<Payment> findByProviderSessionId(String sessionId) {
        return jpaRepository.findByProviderSessionId(sessionId)
                .map(mapper::toDomain);
    }

    @Override
    public Optional<Payment> findByProviderSessionIdForUpdate(String sessionId) {
        return jpaRepository.findByProviderPaymentIdForUpdate(sessionId)
                .map(mapper::toDomain);
    }

    @Override
    public List<PaymentHistoryItem> getPaymentByUserId(Long userId) {
        return jpaRepository.getPaymentByUserId(userId);
    }

    @Override
    public List<PaymentHistoryItem> getPaymentByUserSubscription(Long userId, Long userSubscriptionId) {
        return jpaRepository.getPaymentByUserSubscription(userId, userSubscriptionId);
    }

    @Override
    public int deletePaymentsBefore(LocalDateTime dateTime) {
        return jpaRepository.deletePaymentsBefore(dateTime);
    }

    @Override
    public int deleteByStatusAndCreatedAtBefore(PaymentStatus status, LocalDateTime threshold) {
        return jpaRepository.deleteByStatusAndCreatedAtBefore(status, threshold);
    }
}