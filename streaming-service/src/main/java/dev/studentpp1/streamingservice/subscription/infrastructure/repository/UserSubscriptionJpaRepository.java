package dev.studentpp1.streamingservice.subscription.infrastructure.repository;

import dev.studentpp1.streamingservice.subscription.infrastructure.entity.SubscriptionPlanEntity;
import dev.studentpp1.streamingservice.subscription.domain.model.SubscriptionStatus;
import dev.studentpp1.streamingservice.subscription.infrastructure.entity.UserSubscriptionEntity;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserSubscriptionJpaRepository extends JpaRepository<UserSubscriptionEntity, Long> {
    List<UserSubscriptionEntity> findAllByStatusAndEndTimeBefore(SubscriptionStatus status, LocalDateTime dateTime);

    // block raw in db while current transaction is not completing
    // to prevent double canceling
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT s FROM UserSubscriptionEntity s WHERE s.id = :id")
    Optional<UserSubscriptionEntity> findByIdWithLock(@Param("id") Long id);

    // clearAutomatically -> clear hibernate 1st level cache
    @Modifying(clearAutomatically = true)
    @Query("UPDATE UserSubscriptionEntity us SET us.status = 'EXPIRED' "
            + "WHERE us.status = 'ACTIVE' AND us.endTime < :now")
    int expireOverdueSubscriptions(@Param("now") LocalDateTime now);

    // flushAutomatically = true -> sync all previous changes to db, do cancel & save to db
    @Modifying(flushAutomatically = true)
    @Query("UPDATE UserSubscriptionEntity u SET u.status = 'CANCELLED' "
            + "WHERE u.plan = :plan AND u.status = 'ACTIVE'")
    void cancelAllByPlan(@Param("plan") SubscriptionPlanEntity plan);

    Optional<UserSubscriptionEntity> findByUserId(Long userId);

    Page<UserSubscriptionEntity> findAllByUserId(Long userId, Pageable pageable);

    @Query("""
    SELECT COUNT(u) > 0
    FROM UserSubscriptionEntity u
    WHERE u.userId IN :userIds
      AND u.plan.id = :planId
      AND u.status = :status
    """)
    boolean existsByUserIdInAndPlanIdAndStatus(
            @Param("userIds") List<Long> userIds,
            @Param("planId") Long planId,
            @Param("status") SubscriptionStatus status
    );
}
