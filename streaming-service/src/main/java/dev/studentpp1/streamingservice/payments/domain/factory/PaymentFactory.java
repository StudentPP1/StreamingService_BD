package dev.studentpp1.streamingservice.payments.domain.factory;

import dev.studentpp1.streamingservice.payments.domain.model.Payment;
import dev.studentpp1.streamingservice.payments.domain.model.vo.Money;

import java.math.BigDecimal;

public class PaymentFactory {

    public Payment createNewPayment(
            String sessionId,
            BigDecimal amount,
            String currency,
            Long userId,
            String productName,
            Long subscriptionId) {
        Money money = new Money(amount, currency);
        Payment payment = Payment.createPending(
                sessionId,
                money,
                userId,
                productName
        );
        if (subscriptionId != null) {
            return payment.withSubscriptionId(subscriptionId);
        }
        return payment;
    }
}