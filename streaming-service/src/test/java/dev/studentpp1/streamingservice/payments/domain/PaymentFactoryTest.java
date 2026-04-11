package dev.studentpp1.streamingservice.payments.domain;

import dev.studentpp1.streamingservice.payments.domain.factory.PaymentFactory;
import dev.studentpp1.streamingservice.payments.domain.model.Payment;
import dev.studentpp1.streamingservice.payments.domain.model.PaymentStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.*;

class PaymentFactoryTest {

    private PaymentFactory paymentFactory;

    @BeforeEach
    void setUp() {
        paymentFactory = new PaymentFactory();
    }

    @Test
    void createNewPayment_withoutSubscriptionId_success() {
        Payment payment = paymentFactory.createNewPayment(
                "sess_001", BigDecimal.valueOf(19.99), "USD", 1L, "Premium Plan", null);

        assertThat(payment.getProviderSessionId()).isEqualTo("sess_001");
        assertThat(payment.getStatus()).isEqualTo(PaymentStatus.PENDING);
        assertThat(payment.getMoney().amount()).isEqualByComparingTo(BigDecimal.valueOf(19.99));
        assertThat(payment.getMoney().currency()).isEqualTo("USD");
        assertThat(payment.getUserId()).isEqualTo(1L);
        assertThat(payment.getProductName()).isEqualTo("Premium Plan");
        assertThat(payment.getUserSubscriptionId()).isNull();
        assertThat(payment.getId()).isNull();
    }

    @Test
    void createNewPayment_withSubscriptionId_linksSubscription() {
        Payment payment = paymentFactory.createNewPayment(
                "sess_002", BigDecimal.valueOf(9.99), "USD", 2L, "Basic Plan", 42L);

        assertThat(payment.getUserSubscriptionId()).isEqualTo(42L);
    }

    @Test
    void createNewPayment_withNullSubscriptionId_subscriptionIdRemainsNull() {
        Payment payment = paymentFactory.createNewPayment(
                "sess_003", BigDecimal.valueOf(5.00), "UAH", 3L, "Trial", null);

        assertThat(payment.getUserSubscriptionId()).isNull();
    }
}