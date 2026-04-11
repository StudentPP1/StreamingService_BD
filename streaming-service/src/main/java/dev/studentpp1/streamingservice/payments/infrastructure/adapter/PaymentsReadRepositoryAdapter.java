package dev.studentpp1.streamingservice.payments.infrastructure.adapter;

import dev.studentpp1.streamingservice.payments.application.read.PaymentsReadRepository;
import dev.studentpp1.streamingservice.payments.infrastructure.repository.PaymentJpaRepository;
import dev.studentpp1.streamingservice.payments.presentation.dto.HistoryPaymentResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PaymentsReadRepositoryAdapter implements PaymentsReadRepository {

    private final PaymentJpaRepository paymentJpaRepository;

    @Override
    public List<HistoryPaymentResponse> findUserPayments(Long userId) {
        return paymentJpaRepository.findAllByUserId(userId).stream()
                .map(payment -> new HistoryPaymentResponse(
                        payment.getStatus(),
                        payment.getPaidAt(),
                        payment.getAmount(),
                        payment.getProductName()
                ))
                .toList();
    }

    @Override
    public List<HistoryPaymentResponse> findUserPaymentsBySubscription(Long userId, Long subscriptionId) {
        return paymentJpaRepository.findAllByUserIdAndUserSubscriptionId(userId, subscriptionId).stream()
                .map(payment -> new HistoryPaymentResponse(
                        payment.getStatus(),
                        payment.getPaidAt(),
                        payment.getAmount(),
                        payment.getProductName()
                ))
                .toList();
    }
}

