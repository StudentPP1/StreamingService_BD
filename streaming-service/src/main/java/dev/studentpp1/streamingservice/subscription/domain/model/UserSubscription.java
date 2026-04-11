package dev.studentpp1.streamingservice.subscription.domain.model;

import dev.studentpp1.streamingservice.subscription.domain.exception.SubscriptionDomainException;

import java.time.LocalDateTime;

public class UserSubscription {
    private final Long id;
    private final Long userId;
    private final Long planId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private SubscriptionStatus status;

    private UserSubscription(Long id, Long userId, Long planId,
                             LocalDateTime startTime, LocalDateTime endTime,
                             SubscriptionStatus status) {
        this.id = id;
        this.userId = userId;
        this.planId = planId;
        this.startTime = startTime;
        this.endTime = endTime;
        this.status = status;
    }

    public static UserSubscription restore(Long id, Long userId, Long planId,
                                           LocalDateTime startTime, LocalDateTime endTime,
                                           SubscriptionStatus status) {
        return new UserSubscription(id, userId, planId, startTime, endTime, status);
    }

    public static UserSubscription create(Long userId, Long planId,
                                          LocalDateTime startTime, Integer durationDays) {
        if (userId == null) throw new SubscriptionDomainException("UserId cannot be null");
        if (planId == null) throw new SubscriptionDomainException("PlanId cannot be null");
        if (startTime == null) throw new SubscriptionDomainException("StartTime cannot be null");
        if (durationDays == null || durationDays < 1)
            throw new SubscriptionDomainException("Duration must be at least 1 day");

        return new UserSubscription(null, userId, planId,
                startTime, startTime.plusDays(durationDays), SubscriptionStatus.ACTIVE);
    }

    public void cancel() {
        if (this.status != SubscriptionStatus.ACTIVE)
            throw new SubscriptionDomainException("Only active subscriptions can be cancelled");
        this.status = SubscriptionStatus.CANCELLED;
    }

    public Long getId() { return id; }
    public Long getUserId() { return userId; }
    public Long getPlanId() { return planId; }
    public LocalDateTime getStartTime() { return startTime; }
    public LocalDateTime getEndTime() { return endTime; }
    public SubscriptionStatus getStatus() { return status; }
}