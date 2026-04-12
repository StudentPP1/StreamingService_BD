package dev.studentpp1.streamingservice.subscription.infrastructure.adapter;

import dev.studentpp1.streamingservice.common.dto.PageResult;
import dev.studentpp1.streamingservice.subscription.application.query.repo.UserSubscriptionReadRepository;
import dev.studentpp1.streamingservice.subscription.application.query.readmodel.UserSubscriptionWithPlanReadModel;
import dev.studentpp1.streamingservice.subscription.infrastructure.entity.UserSubscriptionEntity;
import dev.studentpp1.streamingservice.subscription.infrastructure.mapper.UserSubscriptionPersistenceMapper;
import dev.studentpp1.streamingservice.subscription.infrastructure.repository.UserSubscriptionJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserSubscriptionReadRepositoryAdapter implements UserSubscriptionReadRepository {

    private final UserSubscriptionJpaRepository userSubscriptionJpaRepository;
    private final UserSubscriptionPersistenceMapper userSubscriptionPersistenceMapper;

    @Override
    public PageResult<UserSubscriptionWithPlanReadModel> findAllByUserId(Long userId, int page, int size) {
        Page<UserSubscriptionEntity> result = userSubscriptionJpaRepository.findAllByUserId(userId, PageRequest.of(page, size));

        return new PageResult<>(
                result.getContent().stream()
                        .map(this::toReadModel)
                        .toList(),
                result.getNumber(),
                result.getSize(),
                result.getTotalElements(),
                result.getTotalPages()
        );
    }

    private UserSubscriptionWithPlanReadModel toReadModel(UserSubscriptionEntity entity) {
        String planName = entity.getPlan() != null ? entity.getPlan().getName() : "Unknown";
        return new UserSubscriptionWithPlanReadModel(
                userSubscriptionPersistenceMapper.toDomain(entity),
                planName
        );
    }
}


