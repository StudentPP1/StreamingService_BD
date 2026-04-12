package dev.studentpp1.streamingservice.payments.infrastructure.adapter;

import dev.studentpp1.streamingservice.payments.application.query.history.PaymentHistoryReadModel;
import dev.studentpp1.streamingservice.payments.application.query.history.PaymentReadRepository;
import dev.studentpp1.streamingservice.payments.infrastructure.entity.PaymentEntity;
import dev.studentpp1.streamingservice.payments.infrastructure.repository.PaymentJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class PaymentReadRepositoryAdapter implements PaymentReadRepository {

    private final PaymentJpaRepository paymentJpaRepository;

    @Override
    public List<PaymentHistoryReadModel> findByUserId(Long userId) {
        return paymentJpaRepository.findAllByUserId(userId).stream()
                .map(this::toReadModel)
                .toList();
    }

    @Override
    public List<PaymentHistoryReadModel> findByUserSubscription(Long userId, Long userSubscriptionId) {
        return paymentJpaRepository.findAllByUserIdAndUserSubscriptionId(userId, userSubscriptionId).stream()
                .map(this::toReadModel)
                .toList();
    }

    private PaymentHistoryReadModel toReadModel(PaymentEntity entity) {
        return new PaymentHistoryReadModel(
                entity.getStatus(),
                entity.getPaidAt(),
                entity.getAmount(),
                entity.getProductName()
        );
    }
}

