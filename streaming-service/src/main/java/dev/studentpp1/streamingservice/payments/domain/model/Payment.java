package dev.studentpp1.streamingservice.payments.domain.model;

import dev.studentpp1.streamingservice.payments.domain.exception.PaymentDomainException;
import dev.studentpp1.streamingservice.payments.domain.model.vo.Money;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class Payment {
    private final Long id;
    private final String providerSessionId;
    private PaymentStatus status;
    private final Money money; // Наш Value Object
    private final LocalDateTime createdAt;
    private LocalDateTime paidAt;
    private final Long userSubscriptionId;
    private final Long userId;
    private final String productName;

    private Payment(Long id, String providerSessionId, PaymentStatus status,
                    Money money, LocalDateTime createdAt,
                    LocalDateTime paidAt, Long userSubscriptionId, Long userId, String productName) {
        this.id = id;
        this.providerSessionId = providerSessionId;
        this.status = status;
        this.money = money;
        this.createdAt = createdAt;
        this.paidAt = paidAt;
        this.userSubscriptionId = userSubscriptionId;
        this.userId = userId;
        this.productName = productName;
    }

    public static Payment restore(Long id, String providerSessionId, PaymentStatus status,
                                  Money money, LocalDateTime createdAt,
                                  LocalDateTime paidAt, Long userSubscriptionId, Long userId, String productName) {
        return new Payment(id, providerSessionId, status, money,
                createdAt, paidAt, userSubscriptionId, userId, productName);
    }

    public static Payment createPending(String providerSessionId, Money money, Long userId, String productName) {
        if (money == null) throw new PaymentDomainException("Money object cannot be null");
        if (providerSessionId == null || providerSessionId.isBlank())
            throw new PaymentDomainException("Provider session ID cannot be blank");
        if (userId == null)
            throw new PaymentDomainException("User ID is required for payment");
        if (productName == null || productName.isBlank())
            throw new PaymentDomainException("Product name is required for payment");

        return new Payment(null, providerSessionId, PaymentStatus.PENDING,
                money, LocalDateTime.now(), null, null, userId, productName);
    }

    public Payment withSubscriptionId(Long subscriptionId) {
        return new Payment(id, providerSessionId, status, money,
                createdAt, paidAt, subscriptionId, userId, productName);
    }

    public void markAsPaid() {
        this.status = PaymentStatus.COMPLETED;
        this.paidAt = LocalDateTime.now();
    }

    public void markAsFailed() {
        this.status = PaymentStatus.FAILED;
    }
}