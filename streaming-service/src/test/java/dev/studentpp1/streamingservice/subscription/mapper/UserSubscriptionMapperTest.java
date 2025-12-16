package dev.studentpp1.streamingservice.subscription.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import dev.studentpp1.streamingservice.subscription.dto.response.UserSubscriptionDto;
import dev.studentpp1.streamingservice.subscription.entity.SubscriptionPlan;
import dev.studentpp1.streamingservice.subscription.entity.SubscriptionStatus;
import dev.studentpp1.streamingservice.subscription.entity.UserSubscription;
import dev.studentpp1.streamingservice.users.entity.AppUser;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {UserSubscriptionMapperImpl.class})
class UserSubscriptionMapperTest {

    @Autowired
    private UserSubscriptionMapper userSubscriptionMapper;

    @Test
    void toDto_mapsAllFieldsCorrectly() {
        AppUser user = AppUser.builder()
            .id(1L)
            .name("John")
            .surname("Doe")
            .email("john@test.com")
            .password("password")
            .birthday(LocalDate.of(1990, 1, 1))
            .build();

        SubscriptionPlan plan = SubscriptionPlan.builder()
            .id(1L)
            .name("PREMIUM")
            .description("Premium plan")
            .price(new BigDecimal("29.99"))
            .duration(30)
            .movies(new HashSet<>())
            .build();

        LocalDateTime startTime = LocalDateTime.of(2025, 1, 1, 10, 0);
        LocalDateTime endTime = LocalDateTime.of(2025, 1, 31, 10, 0);

        UserSubscription subscription = UserSubscription.builder()
            .id(42L)
            .user(user)
            .plan(plan)
            .startTime(startTime)
            .endTime(endTime)
            .status(SubscriptionStatus.ACTIVE)
            .build();

        UserSubscriptionDto dto = userSubscriptionMapper.toDto(subscription);

        assertThat(dto).isNotNull();
        assertThat(dto.id()).isEqualTo(42L);
        assertThat(dto.startTime()).isEqualTo(startTime);
        assertThat(dto.endTime()).isEqualTo(endTime);
        assertThat(dto.status()).isEqualTo(SubscriptionStatus.ACTIVE);
        assertThat(dto.planName()).isEqualTo("PREMIUM");
    }

    @Test
    void toDto_mapsPlanNameFromNestedPlan() {
        SubscriptionPlan plan = SubscriptionPlan.builder()
            .id(1L)
            .name("BASIC")
            .description("Basic plan")
            .price(new BigDecimal("9.99"))
            .duration(7)
            .movies(new HashSet<>())
            .build();

        UserSubscription subscription = UserSubscription.builder()
            .id(1L)
            .plan(plan)
            .startTime(LocalDateTime.now())
            .endTime(LocalDateTime.now().plusDays(7))
            .status(SubscriptionStatus.ACTIVE)
            .build();

        UserSubscriptionDto dto = userSubscriptionMapper.toDto(subscription);

        assertThat(dto.planName()).isEqualTo("BASIC");
    }

    @Test
    void toDto_withNullPlan_handlesGracefully() {
        UserSubscription subscription = UserSubscription.builder()
            .id(1L)
            .plan(null)
            .startTime(LocalDateTime.now())
            .endTime(LocalDateTime.now().plusDays(30))
            .status(SubscriptionStatus.ACTIVE)
            .build();

        UserSubscriptionDto dto = userSubscriptionMapper.toDto(subscription);

        assertThat(dto).isNotNull();
        assertThat(dto.id()).isEqualTo(1L);
        assertThat(dto.planName()).isNull();
    }

    @Test
    void toDto_withNullFields_handlesNullValueCheckStrategy() {
        SubscriptionPlan plan = SubscriptionPlan.builder()
            .name("TEST")
            .build();

        UserSubscription subscription = UserSubscription.builder()
            .id(null)
            .plan(plan)
            .startTime(LocalDateTime.now())
            .endTime(LocalDateTime.now().plusDays(30))
            .status(SubscriptionStatus.ACTIVE)
            .build();

        UserSubscriptionDto dto = userSubscriptionMapper.toDto(subscription);

        assertThat(dto).isNotNull();
        assertThat(dto.id()).isNull();
        assertThat(dto.planName()).isEqualTo("TEST");
        assertThat(dto.status()).isEqualTo(SubscriptionStatus.ACTIVE);
    }

    @Test
    void toDto_preservesStatusEnum() {
        SubscriptionPlan plan = SubscriptionPlan.builder()
            .name("STANDARD")
            .build();

        UserSubscription activeSubscription = UserSubscription.builder()
            .id(1L)
            .plan(plan)
            .startTime(LocalDateTime.now())
            .endTime(LocalDateTime.now().plusDays(30))
            .status(SubscriptionStatus.ACTIVE)
            .build();

        UserSubscription expiredSubscription = UserSubscription.builder()
            .id(2L)
            .plan(plan)
            .startTime(LocalDateTime.now().minusDays(60))
            .endTime(LocalDateTime.now().minusDays(30))
            .status(SubscriptionStatus.EXPIRED)
            .build();

        UserSubscription cancelledSubscription = UserSubscription.builder()
            .id(3L)
            .plan(plan)
            .startTime(LocalDateTime.now())
            .endTime(LocalDateTime.now().plusDays(30))
            .status(SubscriptionStatus.CANCELLED)
            .build();

        UserSubscriptionDto activeDto = userSubscriptionMapper.toDto(activeSubscription);
        UserSubscriptionDto expiredDto = userSubscriptionMapper.toDto(expiredSubscription);
        UserSubscriptionDto cancelledDto = userSubscriptionMapper.toDto(cancelledSubscription);

        assertThat(activeDto.status()).isEqualTo(SubscriptionStatus.ACTIVE);
        assertThat(expiredDto.status()).isEqualTo(SubscriptionStatus.EXPIRED);
        assertThat(cancelledDto.status()).isEqualTo(SubscriptionStatus.CANCELLED);
    }

    @Test
    void toDto_preservesDateTimeFields() {
        SubscriptionPlan plan = SubscriptionPlan.builder()
            .name("PREMIUM")
            .build();

        LocalDateTime specificStartTime = LocalDateTime.of(2025, 6, 15, 14, 30, 45);
        LocalDateTime specificEndTime = LocalDateTime.of(2025, 7, 15, 14, 30, 45);

        UserSubscription subscription = UserSubscription.builder()
            .id(1L)
            .plan(plan)
            .startTime(specificStartTime)
            .endTime(specificEndTime)
            .status(SubscriptionStatus.ACTIVE)
            .build();

        UserSubscriptionDto dto = userSubscriptionMapper.toDto(subscription);

        assertThat(dto.startTime()).isEqualTo(specificStartTime);
        assertThat(dto.endTime()).isEqualTo(specificEndTime);
        assertThat(dto.startTime()).isNotEqualTo(dto.endTime());
    }
}

