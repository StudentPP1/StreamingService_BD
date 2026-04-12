package dev.studentpp1.streamingservice.subscription.infrastructure.adapter;

import dev.studentpp1.streamingservice.common.dto.PageResult;
import dev.studentpp1.streamingservice.subscription.domain.model.SubscriptionStatus;
import dev.studentpp1.streamingservice.subscription.domain.model.UserSubscription;
import dev.studentpp1.streamingservice.subscription.domain.repository.UserSubscriptionRepository;
import dev.studentpp1.streamingservice.subscription.infrastructure.entity.SubscriptionPlanEntity;
import dev.studentpp1.streamingservice.subscription.infrastructure.entity.UserSubscriptionEntity;
import dev.studentpp1.streamingservice.subscription.infrastructure.mapper.UserSubscriptionPersistenceMapper;
import dev.studentpp1.streamingservice.subscription.infrastructure.repository.SubscriptionPlanJpaRepository;
import dev.studentpp1.streamingservice.subscription.infrastructure.repository.UserSubscriptionJpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Component
public class UserSubscriptionRepositoryAdapter implements UserSubscriptionRepository {

    private final UserSubscriptionJpaRepository jpaRepository;
    private final SubscriptionPlanJpaRepository planJpaRepository;
    private final UserSubscriptionPersistenceMapper mapper;

    public UserSubscriptionRepositoryAdapter(
            UserSubscriptionJpaRepository jpaRepository,
            SubscriptionPlanJpaRepository planJpaRepository,
            UserSubscriptionPersistenceMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.planJpaRepository = planJpaRepository;
        this.mapper = mapper;
    }

    @Override
    public Optional<UserSubscription> findById(Long id) {
        return jpaRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public Optional<UserSubscription> findByIdWithLock(Long id) {
        return jpaRepository.findByIdWithLock(id).map(mapper::toDomain);
    }

    @Override
    public List<UserSubscription> findByUserId(Long userId) {
        return jpaRepository.findByUserId(userId).stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public PageResult<UserSubscription> findAllByUserId(Long userId, int page, int size) {
        Page<UserSubscriptionEntity> entityPage =
                jpaRepository.findAllByUserId(userId, PageRequest.of(page, size));
        return new PageResult<>(
                entityPage.getContent().stream().map(mapper::toDomain).toList(),
                entityPage.getNumber(),
                entityPage.getSize(),
                entityPage.getTotalElements(),
                entityPage.getTotalPages()
        );
    }

    @Override
    public UserSubscription save(UserSubscription domain) {
        UserSubscriptionEntity entity = mapper.toEntity(domain);
        entity.setUserId(domain.getUserId());
        if (domain.getPlanId() != null) {
            entity.setPlan(planJpaRepository.findById(domain.getPlanId()).orElseThrow());
        }
        return mapper.toDomain(jpaRepository.save(entity));
    }

    @Override
    public List<UserSubscription> saveAll(List<UserSubscription> subscriptions) {
        List<UserSubscriptionEntity> entities = subscriptions.stream()
                .map(domain -> {
                    UserSubscriptionEntity entity = mapper.toEntity(domain);
                    entity.setUserId(domain.getUserId());
                    entity.setPlan(planJpaRepository.findById(domain.getPlanId()).orElseThrow());
                    return entity;
                })
                .toList();
        return jpaRepository.saveAll(entities).stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public int expireOverdueSubscriptions(LocalDateTime now) {
        return jpaRepository.expireOverdueSubscriptions(now);
    }

    @Override
    public int cancelAllByPlanId(Long planId) {
        SubscriptionPlanEntity plan = planJpaRepository.findById(planId).orElseThrow();
        jpaRepository.cancelAllByPlan(plan);
        return 0;
    }

    @Override
    public boolean existsByUserIdInAndPlanIdAndStatus(List<Long> userIds, Long planId, SubscriptionStatus status) {
        return jpaRepository.existsByUserIdInAndPlanIdAndStatus(userIds, planId, status);
    }
}