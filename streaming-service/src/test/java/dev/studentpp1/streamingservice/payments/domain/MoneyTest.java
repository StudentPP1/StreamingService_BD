package dev.studentpp1.streamingservice.payments.domain;

import dev.studentpp1.streamingservice.payments.domain.exception.PaymentDomainException;
import dev.studentpp1.streamingservice.payments.domain.model.vo.Money;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.*;

class MoneyTest {

    @Test
    void create_validMoney_success() {
        Money money = new Money(BigDecimal.valueOf(9.99), "USD");
        assertThat(money.amount()).isEqualByComparingTo(BigDecimal.valueOf(9.99));
        assertThat(money.currency()).isEqualTo("USD");
    }

    @Test
    void create_zeroAmount_throwsDomainException() {
        assertThatThrownBy(() -> new Money(BigDecimal.ZERO, "USD"))
                .isInstanceOf(PaymentDomainException.class)
                .hasMessageContaining("Amount must be positive");
    }

    @Test
    void create_negativeAmount_throwsDomainException() {
        assertThatThrownBy(() -> new Money(BigDecimal.valueOf(-1.0), "USD"))
                .isInstanceOf(PaymentDomainException.class)
                .hasMessageContaining("Amount must be positive");
    }

    @Test
    void create_nullAmount_throwsDomainException() {
        assertThatThrownBy(() -> new Money(null, "USD"))
                .isInstanceOf(PaymentDomainException.class);
    }

    @Test
    void create_blankCurrency_throwsDomainException() {
        assertThatThrownBy(() -> new Money(BigDecimal.valueOf(10.0), ""))
                .isInstanceOf(PaymentDomainException.class)
                .hasMessageContaining("Currency is required");
    }

    @Test
    void create_nullCurrency_throwsDomainException() {
        assertThatThrownBy(() -> new Money(BigDecimal.valueOf(10.0), null))
                .isInstanceOf(PaymentDomainException.class);
    }

    @Test
    void isGreaterThanZero_positiveAmount_returnsTrue() {
        Money money = Money.of(BigDecimal.valueOf(5.0), "USD");
        assertThat(money.isGreaterThanZero()).isTrue();
    }
}