package dev.studentpp1.streamingservice.payments.domain;

import dev.studentpp1.streamingservice.payments.domain.exception.PaymentDomainException;
import dev.studentpp1.streamingservice.payments.domain.model.Payment;
import dev.studentpp1.streamingservice.payments.domain.model.PaymentStatus;
import dev.studentpp1.streamingservice.payments.domain.model.vo.Money;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.*;

class PaymentTest {

    private static final Money MONEY = new Money(BigDecimal.valueOf(9.99), "USD");

    @Test
    void createPending_validData_success() {
        Payment payment = Payment.createPending("session_abc", MONEY, 1L, "Premium Plan");

        assertThat(payment.getId()).isNull();
        assertThat(payment.getProviderSessionId()).isEqualTo("session_abc");
        assertThat(payment.getStatus()).isEqualTo(PaymentStatus.PENDING);
        assertThat(payment.getMoney()).isEqualTo(MONEY);
        assertThat(payment.getUserId()).isEqualTo(1L);
        assertThat(payment.getProductName()).isEqualTo("Premium Plan");
        assertThat(payment.getPaidAt()).isNull();
        assertThat(payment.getUserSubscriptionId()).isNull();
        assertThat(payment.getCreatedAt()).isNotNull();
    }

    @Test
    void createPending_nullSessionId_throwsDomainException() {
        assertThatThrownBy(() -> Payment.createPending(null, MONEY, 1L, "Plan"))
                .isInstanceOf(PaymentDomainException.class)
                .hasMessageContaining("Provider session ID cannot be blank");
    }

    @Test
    void createPending_blankSessionId_throwsDomainException() {
        assertThatThrownBy(() -> Payment.createPending("", MONEY, 1L, "Plan"))
                .isInstanceOf(PaymentDomainException.class)
                .hasMessageContaining("Provider session ID cannot be blank");
    }

    @Test
    void createPending_nullMoney_throwsDomainException() {
        assertThatThrownBy(() -> Payment.createPending("session_abc", null, 1L, "Plan"))
                .isInstanceOf(PaymentDomainException.class)
                .hasMessageContaining("Money object cannot be null");
    }

    @Test
    void createPending_nullUserId_throwsDomainException() {
        assertThatThrownBy(() -> Payment.createPending("session_abc", MONEY, null, "Plan"))
                .isInstanceOf(PaymentDomainException.class)
                .hasMessageContaining("User ID is required");
    }

    @Test
    void createPending_blankProductName_throwsDomainException() {
        assertThatThrownBy(() -> Payment.createPending("session_abc", MONEY, 1L, ""))
                .isInstanceOf(PaymentDomainException.class)
                .hasMessageContaining("Product name is required");
    }

    @Test
    void markAsPaid_changesStatusToCompleted() {
        Payment payment = Payment.createPending("session_abc", MONEY, 1L, "Premium Plan");

        payment.markAsPaid();

        assertThat(payment.getStatus()).isEqualTo(PaymentStatus.COMPLETED);
        assertThat(payment.getPaidAt()).isNotNull();
    }

    @Test
    void markAsFailed_changesStatusToFailed() {
        Payment payment = Payment.createPending("session_abc", MONEY, 1L, "Premium Plan");

        payment.markAsFailed();

        assertThat(payment.getStatus()).isEqualTo(PaymentStatus.FAILED);
    }

    @Test
    void assignSubscription_setsSubscriptionId() {
        Payment payment = Payment.createPending("session_abc", MONEY, 1L, "Premium Plan");

        payment.assignSubscription(42L);

        assertThat(payment.getUserSubscriptionId()).isEqualTo(42L);
    }

    @Test
    void restore_setsAllFields() {
        LocalDateTime now = LocalDateTime.now();
        Payment payment = Payment.restore(
                10L, "session_abc", PaymentStatus.COMPLETED,
                MONEY, now, now.plusMinutes(5), 5L, 1L, "Premium Plan");

        assertThat(payment.getId()).isEqualTo(10L);
        assertThat(payment.getStatus()).isEqualTo(PaymentStatus.COMPLETED);
        assertThat(payment.getUserSubscriptionId()).isEqualTo(5L);
        assertThat(payment.getPaidAt()).isEqualTo(now.plusMinutes(5));
    }
}