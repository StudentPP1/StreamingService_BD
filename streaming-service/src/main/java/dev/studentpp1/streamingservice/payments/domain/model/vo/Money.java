package dev.studentpp1.streamingservice.payments.domain.model.vo;

import dev.studentpp1.streamingservice.payments.domain.exception.PaymentDomainException;
import java.math.BigDecimal;

public record Money(BigDecimal amount, String currency) {
    public Money {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new PaymentDomainException("Amount must be positive");
        }
        if (currency == null || currency.isBlank()) {
            throw new PaymentDomainException("Currency is required");
        }
    }

    public static Money of(BigDecimal amount, String currency) {
        return new Money(amount, currency);
    }

    public boolean isGreaterThanZero() {
        return amount.compareTo(BigDecimal.ZERO) > 0;
    }
}