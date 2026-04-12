package dev.studentpp1.streamingservice.subscription.presentation.mapper;

import dev.studentpp1.streamingservice.subscription.domain.model.UserSubscription;
import dev.studentpp1.streamingservice.subscription.presentation.dto.UserSubscriptionDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserSubscriptionPresentationMapper {

    @Mapping(target = "planName", ignore = true)
    UserSubscriptionDto toDto(UserSubscription subscription);

    default UserSubscriptionDto toDto(UserSubscription subscription, String planName) {
        return new UserSubscriptionDto(
                subscription.getId(),
                subscription.getStartTime(),
                subscription.getEndTime(),
                subscription.getStatus(),
                planName
        );
    }
}