package dev.studentpp1.streamingservice.payments.infrastructure.mapper;

import dev.studentpp1.streamingservice.payments.domain.model.Payment;
import dev.studentpp1.streamingservice.payments.domain.model.vo.Money;
import dev.studentpp1.streamingservice.payments.infrastructure.entity.PaymentEntity;
import org.springframework.stereotype.Component;
@Component
public class PaymentPersistenceMapper {

    public Payment toDomain(PaymentEntity entity) {
        Money money = new Money(entity.getAmount(), entity.getCurrency());

        return Payment.restore(
                entity.getId(),
                entity.getProviderSessionId(),
                entity.getStatus(),
                money,
                entity.getCreatedAt(),
                entity.getPaidAt(),
                entity.getUserSubscriptionId(),
                entity.getUserId(),
                entity.getProductName()
        );
    }

    public PaymentEntity toEntity(Payment domain) {
        PaymentEntity entity = new PaymentEntity();
        entity.setId(domain.getId());
        entity.setProviderSessionId(domain.getProviderSessionId());
        entity.setStatus(domain.getStatus());
        entity.setAmount(domain.getMoney().amount());
        entity.setCurrency(domain.getMoney().currency());
        entity.setCreatedAt(domain.getCreatedAt());
        entity.setPaidAt(domain.getPaidAt());
        entity.setUserSubscriptionId(domain.getUserSubscriptionId());
        entity.setUserId(domain.getUserId());
        entity.setProductName(domain.getProductName());
        return entity;
    }
}