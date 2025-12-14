package dev.studentpp1.streamingservice.subscription.repository;

import dev.studentpp1.streamingservice.subscription.entity.SubscriptionStatus;
import dev.studentpp1.streamingservice.subscription.entity.UserSubscription;
import dev.studentpp1.streamingservice.users.entity.AppUser;
import jakarta.persistence.LockModeType;
import java.time.LocalDateTime;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserSubscriptionRepository extends JpaRepository<UserSubscription, Long> {
    List<UserSubscription> findByUser(AppUser user);

    List<UserSubscription> findAllByStatusAndEndTimeBefore(SubscriptionStatus status, LocalDateTime dateTime);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT s FROM UserSubscription s WHERE s.id = :id")
    Optional<UserSubscription> findByIdWithLock(@Param("id") Long id);

    @Modifying(clearAutomatically = true)
    @Query("UPDATE UserSubscription us SET us.status = 'EXPIRED' " +
        "WHERE us.status = 'ACTIVE' AND us.endTime < :now")
    int expireOverdueSubscriptions(@Param("now") LocalDateTime now);
}
