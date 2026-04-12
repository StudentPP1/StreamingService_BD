package dev.studentpp1.streamingservice.payments.integration;

import dev.studentpp1.streamingservice.AbstractPostgresContainerTest;
import dev.studentpp1.streamingservice.payments.application.query.history.GetSubscriptionPaymentsQuery;
import dev.studentpp1.streamingservice.payments.application.query.history.GetUserPaymentsQuery;
import dev.studentpp1.streamingservice.payments.application.query.history.PaymentHistoryReadModel;
import dev.studentpp1.streamingservice.payments.application.query.history.PaymentQueryHandler;
import dev.studentpp1.streamingservice.payments.domain.model.PaymentStatus;
import dev.studentpp1.streamingservice.payments.infrastructure.entity.PaymentEntity;
import dev.studentpp1.streamingservice.payments.infrastructure.repository.PaymentJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.DirtiesContext;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class PaymentQueryHandlerIntegrationTest extends AbstractPostgresContainerTest {

    @Autowired
    private PaymentQueryHandler paymentQueryHandler;

    @Autowired
    private PaymentJpaRepository paymentJpaRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUp() {
        jdbcTemplate.execute("TRUNCATE TABLE payment RESTART IDENTITY CASCADE");
    }

    @Test
    void getUserPayments_returnsOnlyUserPayments() {
        paymentJpaRepository.save(payment(1L, 1L, "Basic Plan", BigDecimal.valueOf(9.99), PaymentStatus.COMPLETED));
        paymentJpaRepository.save(payment(1L, 6L, "Premium Plan", BigDecimal.valueOf(19.99), PaymentStatus.PENDING));
        paymentJpaRepository.save(payment(2L, 2L, "Other User Plan", BigDecimal.valueOf(29.99), PaymentStatus.COMPLETED));

        List<PaymentHistoryReadModel> result = paymentQueryHandler.handle(new GetUserPaymentsQuery(1L));

        assertThat(result).hasSize(2);
        assertThat(result).extracting(PaymentHistoryReadModel::subscriptionName)
                .containsExactlyInAnyOrder("Basic Plan", "Premium Plan");
    }

    @Test
    void getSubscriptionPayments_returnsOnlyMatchingSubscription() {
        paymentJpaRepository.save(payment(1L, 1L, "Basic Plan", BigDecimal.valueOf(9.99), PaymentStatus.COMPLETED));
        paymentJpaRepository.save(payment(1L, 6L, "Premium Plan", BigDecimal.valueOf(19.99), PaymentStatus.COMPLETED));
        paymentJpaRepository.save(payment(2L, 2L, "Other User Plan", BigDecimal.valueOf(29.99), PaymentStatus.COMPLETED));

        List<PaymentHistoryReadModel> result = paymentQueryHandler
                .handle(new GetSubscriptionPaymentsQuery(1L, 1L));

        assertThat(result).hasSize(1);
        assertThat(result.getFirst().subscriptionName()).isEqualTo("Basic Plan");
    }

    private PaymentEntity payment(Long userId,
                                  Long subscriptionId,
                                  String productName,
                                  BigDecimal amount,
                                  PaymentStatus status) {
        return PaymentEntity.builder()
                .providerSessionId("sess_" + UUID.randomUUID())
                .userId(userId)
                .userSubscriptionId(subscriptionId)
                .productName(productName)
                .amount(amount)
                .currency("USD")
                .status(status)
                .paidAt(LocalDateTime.now())
                .build();
    }
}


