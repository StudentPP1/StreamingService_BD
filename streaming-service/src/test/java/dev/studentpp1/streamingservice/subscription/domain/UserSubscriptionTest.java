package dev.studentpp1.streamingservice.subscription.domain;

import dev.studentpp1.streamingservice.subscription.domain.exception.SubscriptionDomainException;
import dev.studentpp1.streamingservice.subscription.domain.model.UserSubscription;
import dev.studentpp1.streamingservice.subscription.domain.model.SubscriptionStatus;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.*;

class UserSubscriptionTest {

    private static final LocalDateTime NOW = LocalDateTime.of(2025, 1, 1, 12, 0);

    @Test
    void create_validSubscription_success() {
        UserSubscription sub = UserSubscription.create(1L, 10L, NOW, 30);

        assertThat(sub.getUserId()).isEqualTo(1L);
        assertThat(sub.getPlanId()).isEqualTo(10L);
        assertThat(sub.getStatus()).isEqualTo(SubscriptionStatus.ACTIVE);
        assertThat(sub.getId()).isNull();
        assertThat(sub.getEndTime()).isEqualTo(NOW.plusDays(30));
    }

    @Test
    void create_nullUserId_throwsDomainException() {
        assertThatThrownBy(() -> UserSubscription.create(null, 10L, NOW, 30))
                .isInstanceOf(SubscriptionDomainException.class)
                .hasMessageContaining("UserId cannot be null");
    }

    @Test
    void create_nullPlanId_throwsDomainException() {
        assertThatThrownBy(() -> UserSubscription.create(1L, null, NOW, 30))
                .isInstanceOf(SubscriptionDomainException.class)
                .hasMessageContaining("PlanId cannot be null");
    }

    @Test
    void create_nullStartTime_throwsDomainException() {
        assertThatThrownBy(() -> UserSubscription.create(1L, 10L, null, 30))
                .isInstanceOf(SubscriptionDomainException.class)
                .hasMessageContaining("StartTime cannot be null");
    }

    @Test
    void create_zeroDuration_throwsDomainException() {
        assertThatThrownBy(() -> UserSubscription.create(1L, 10L, NOW, 0))
                .isInstanceOf(SubscriptionDomainException.class)
                .hasMessageContaining("Duration must be at least 1 day");
    }

    @Test
    void cancel_activeSubscription_success() {
        UserSubscription sub = UserSubscription.create(1L, 10L, NOW, 30);
        sub.cancel();
        assertThat(sub.getStatus()).isEqualTo(SubscriptionStatus.CANCELLED);
    }

    @Test
    void cancel_alreadyCancelled_throwsDomainException() {
        UserSubscription sub = UserSubscription.restore(
                1L, 1L, 10L, NOW, NOW.plusDays(30), SubscriptionStatus.CANCELLED);

        assertThatThrownBy(sub::cancel)
                .isInstanceOf(SubscriptionDomainException.class)
                .hasMessageContaining("Only active subscriptions can be cancelled");
    }
}