package dev.studentpp1.streamingservice.subscription.presentation.mapper;

import dev.studentpp1.streamingservice.subscription.domain.model.SubscriptionMovie;
import dev.studentpp1.streamingservice.subscription.domain.model.SubscriptionPlan;
import dev.studentpp1.streamingservice.subscription.presentation.dto.SubscriptionPlanDetailsDto;
import dev.studentpp1.streamingservice.subscription.presentation.dto.SubscriptionPlanMovieDto;
import dev.studentpp1.streamingservice.subscription.presentation.dto.SubscriptionPlanSummaryDto;
import org.mapstruct.Mapper;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
@Mapper(componentModel = "spring")
public interface SubscriptionPlanPresentationMapper {

    SubscriptionPlanSummaryDto toSummaryDto(SubscriptionPlan plan);

    default SubscriptionPlanDetailsDto toDetailsDto(SubscriptionPlan plan, List<SubscriptionMovie> movies) {
        Set<SubscriptionPlanMovieDto> movieDtos = movies.stream()
                .map(m -> new SubscriptionPlanMovieDto(
                        m.id(),
                        m.title(),
                        m.description(),
                        m.year(),
                        m.rating()
                ))
                .collect(Collectors.toSet());

        return new SubscriptionPlanDetailsDto(
                plan.getId(),
                plan.getName(),
                plan.getDescription(),
                plan.getPrice(),
                plan.getDuration(),
                movieDtos
        );
    }
}