package dev.studentpp1.streamingservice.subscription.domain.repository;

import dev.studentpp1.streamingservice.common.dto.PageResult;
import dev.studentpp1.streamingservice.subscription.domain.model.SubscriptionPlan;

import java.util.Optional;
public interface SubscriptionPlanRepository {
    Optional<SubscriptionPlan> findById(Long id);
    Optional<SubscriptionPlan> findByName(String name);
    Optional<SubscriptionPlan> findByIdWithMovies(Long id);
    Optional<SubscriptionPlan> findByIdWithLock(Long id);
    PageResult<SubscriptionPlan> findAll(int page, int size);
    PageResult<SubscriptionPlan> findAllByNameContaining(String search, int page, int size);
    SubscriptionPlan save(SubscriptionPlan plan);
    void delete(SubscriptionPlan plan);
    boolean existsByName(String name);
}