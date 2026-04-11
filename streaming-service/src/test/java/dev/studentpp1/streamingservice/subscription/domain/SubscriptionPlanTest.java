package dev.studentpp1.streamingservice.subscription.domain;

import dev.studentpp1.streamingservice.subscription.domain.exception.SubscriptionDomainException;
import dev.studentpp1.streamingservice.subscription.domain.model.SubscriptionPlan;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Set;

import static org.assertj.core.api.Assertions.*;

class SubscriptionPlanTest {

    @Test
    void create_validPlan_success() {
        SubscriptionPlan plan = SubscriptionPlan.create(
                "Basic", "Basic plan", BigDecimal.valueOf(9.99), 30);

        assertThat(plan.getName()).isEqualTo("Basic");
        assertThat(plan.getDescription()).isEqualTo("Basic plan");
        assertThat(plan.getPrice()).isEqualByComparingTo(BigDecimal.valueOf(9.99));
        assertThat(plan.getDuration()).isEqualTo(30);
        assertThat(plan.getId()).isNull();
        assertThat(plan.getMovieIds()).isEmpty();
    }

    @Test
    void create_blankName_throwsDomainException() {
        assertThatThrownBy(() ->
                SubscriptionPlan.create("", "desc", BigDecimal.valueOf(9.99), 30))
                .isInstanceOf(SubscriptionDomainException.class)
                .hasMessageContaining("name cannot be blank");
    }

    @Test
    void create_blankDescription_throwsDomainException() {
        assertThatThrownBy(() ->
                SubscriptionPlan.create("Basic", "", BigDecimal.valueOf(9.99), 30))
                .isInstanceOf(SubscriptionDomainException.class)
                .hasMessageContaining("description cannot be blank");
    }

    @Test
    void create_negativePrice_throwsDomainException() {
        assertThatThrownBy(() ->
                SubscriptionPlan.create("Basic", "desc", BigDecimal.valueOf(-1.0), 30))
                .isInstanceOf(SubscriptionDomainException.class)
                .hasMessageContaining("price cannot be negative");
    }

    @Test
    void create_zeroDuration_throwsDomainException() {
        assertThatThrownBy(() ->
                SubscriptionPlan.create("Basic", "desc", BigDecimal.valueOf(9.99), 0))
                .isInstanceOf(SubscriptionDomainException.class)
                .hasMessageContaining("duration must be at least 1 day");
    }

    @Test
    void create_freePlan_success() {
        SubscriptionPlan plan = SubscriptionPlan.create(
                "Free", "Free plan", BigDecimal.ZERO, 7);
        assertThat(plan.getPrice()).isEqualByComparingTo(BigDecimal.ZERO);
    }

    @Test
    void update_validData_success() {
        SubscriptionPlan plan = SubscriptionPlan.restore(
                1L, "Basic", "Basic plan",
                BigDecimal.valueOf(9.99), 30, Set.of(), 0L);

        plan.update("Premium", "Premium plan", BigDecimal.valueOf(19.99), 60);

        assertThat(plan.getName()).isEqualTo("Premium");
        assertThat(plan.getDuration()).isEqualTo(60);
    }

    @Test
    void addMovies_addsToSet() {
        SubscriptionPlan plan = SubscriptionPlan.restore(
                1L, "Basic", "desc", BigDecimal.valueOf(9.99), 30, Set.of(), 0L);

        plan.addMovies(Set.of(1L, 2L, 3L));

        assertThat(plan.getMovieIds()).containsExactlyInAnyOrder(1L, 2L, 3L);
    }

    @Test
    void removeMovies_removesFromSet() {
        SubscriptionPlan plan = SubscriptionPlan.restore(
                1L, "Basic", "desc", BigDecimal.valueOf(9.99), 30,
                Set.of(1L, 2L, 3L), 0L);

        boolean removed = plan.removeMovies(Set.of(1L, 2L));

        assertThat(removed).isTrue();
        assertThat(plan.getMovieIds()).containsOnly(3L);
    }

    @Test
    void removeMovies_notPresentIds_returnsFalse() {
        SubscriptionPlan plan = SubscriptionPlan.restore(
                1L, "Basic", "desc", BigDecimal.valueOf(9.99), 30,
                Set.of(1L), 0L);

        boolean removed = plan.removeMovies(Set.of(99L));

        assertThat(removed).isFalse();
    }
}