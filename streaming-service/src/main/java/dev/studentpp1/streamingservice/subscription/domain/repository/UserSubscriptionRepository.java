package dev.studentpp1.streamingservice.subscription.domain.repository;

import dev.studentpp1.streamingservice.common.dto.PageResult;
import dev.studentpp1.streamingservice.subscription.domain.model.SubscriptionStatus;
import dev.studentpp1.streamingservice.subscription.domain.model.UserSubscription;

import java.util.List;
import java.util.Optional;

public interface UserSubscriptionRepository {
    Optional<UserSubscription> findById(Long id);
    Optional<UserSubscription> findByIdWithLock(Long id);
    List<UserSubscription> findByUserId(Long userId);
    PageResult<UserSubscription> findAllByUserId(Long userId, int page, int size);
    UserSubscription save(UserSubscription subscription);
    List<UserSubscription> saveAll(List<UserSubscription> subscriptions);
    int expireOverdueSubscriptions(java.time.LocalDateTime now);
    int cancelAllByPlanId(Long planId);
    boolean existsByUserIdInAndPlanIdAndStatus(List<Long> userIds, Long planId, SubscriptionStatus status);
}