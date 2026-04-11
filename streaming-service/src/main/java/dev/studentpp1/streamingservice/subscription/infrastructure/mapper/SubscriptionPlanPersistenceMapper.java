package dev.studentpp1.streamingservice.subscription.infrastructure.mapper;

import dev.studentpp1.streamingservice.subscription.domain.model.SubscriptionPlan;
import dev.studentpp1.streamingservice.subscription.infrastructure.entity.SubscriptionPlanEntity;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

@Component
public class SubscriptionPlanPersistenceMapper {

    public SubscriptionPlan toDomain(SubscriptionPlanEntity entity) {
        Set<Long> movieIds = entity.getMovieIds() == null
                ? new HashSet<>()
                : new HashSet<>(entity.getMovieIds());

        return SubscriptionPlan.restore(
                entity.getId(),
                entity.getName(),
                entity.getDescription(),
                entity.getPrice(),
                entity.getDuration(),
                movieIds,
                entity.getVersion()
        );
    }

    public SubscriptionPlanEntity toEntity(SubscriptionPlan domain) {
        SubscriptionPlanEntity entity = new SubscriptionPlanEntity();
        entity.setId(domain.getId());
        entity.setName(domain.getName());
        entity.setDescription(domain.getDescription());
        entity.setPrice(domain.getPrice());
        entity.setDuration(domain.getDuration());
        entity.setVersion(domain.getVersion());
        entity.setMovieIds(new HashSet<>(domain.getMovieIds()));
        return entity;
    }
}