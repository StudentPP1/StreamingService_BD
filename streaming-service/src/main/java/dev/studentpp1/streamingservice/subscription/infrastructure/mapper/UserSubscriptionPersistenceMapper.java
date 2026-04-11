package dev.studentpp1.streamingservice.subscription.infrastructure.mapper;

import dev.studentpp1.streamingservice.subscription.domain.model.UserSubscription;
import dev.studentpp1.streamingservice.subscription.infrastructure.entity.UserSubscriptionEntity;
import org.springframework.stereotype.Component;

@Component
public class UserSubscriptionPersistenceMapper {

    public UserSubscription toDomain(UserSubscriptionEntity entity) {
        return UserSubscription.restore(
                entity.getId(),
                entity.getUser() != null ? entity.getUser().getId() : null,
                entity.getPlan() != null ? entity.getPlan().getId() : null,
                entity.getStartTime(),
                entity.getEndTime(),
                dev.studentpp1.streamingservice.subscription.domain.model.SubscriptionStatus.valueOf(entity.getStatus().name())
        );
    }

    public UserSubscriptionEntity toEntity(UserSubscription domain) {
        UserSubscriptionEntity entity = new UserSubscriptionEntity();
        entity.setId(domain.getId());
        entity.setStartTime(domain.getStartTime());
        entity.setEndTime(domain.getEndTime());
        entity.setStatus(domain.getStatus());
        return entity;
    }
}