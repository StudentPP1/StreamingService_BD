package dev.studentpp1.streamingservice.subscription.infrastructure.entity;

import dev.studentpp1.streamingservice.subscription.domain.model.SubscriptionStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.AssertTrue;
import lombok.*;
import org.hibernate.annotations.JdbcType;
import org.hibernate.dialect.type.PostgreSQLEnumJdbcType;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
@Entity
@Table(name = "user_subscription")
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class UserSubscriptionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_subscription_id")
    @EqualsAndHashCode.Include
    private Long id;

    @Column(name = "start_time", nullable = false)
    private LocalDateTime startTime;

    @Column(name = "end_time", nullable = false)
    private LocalDateTime endTime;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @JdbcType(PostgreSQLEnumJdbcType.class)
    private SubscriptionStatus status;

    @ManyToOne(optional = false)
    @JoinColumn(name = "subscription_plan_id")
    private SubscriptionPlanEntity plan;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @AssertTrue(message = "End time must be after start time")
    private boolean isEndTimeAfterStartTime() {
        return endTime != null && startTime != null && endTime.isAfter(startTime);
    }

    @PrePersist
    private void onCreate() {
        if (startTime == null) {
            startTime = LocalDateTime.now();
        }
    }
}
