package dev.studentpp1.streamingservice.subscription.application.query;

import dev.studentpp1.streamingservice.common.dto.PageResult;
import dev.studentpp1.streamingservice.subscription.application.query.readmodel.SubscriptionPlanDetailsReadModel;
import dev.studentpp1.streamingservice.subscription.application.query.readmodel.SubscriptionPlanMovieReadModel;
import dev.studentpp1.streamingservice.subscription.application.query.readmodel.SubscriptionPlanWithMoviesReadModel;
import dev.studentpp1.streamingservice.subscription.application.query.repo.SubscriptionPlanReadRepository;
import dev.studentpp1.streamingservice.subscription.application.query.readmodel.SubscriptionPlanSummaryReadModel;
import dev.studentpp1.streamingservice.subscription.domain.exception.SubscriptionPlanNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class SubscriptionPlanQueryHandler {

    private final SubscriptionPlanReadRepository subscriptionPlanReadRepository;

    public PageResult<SubscriptionPlanSummaryReadModel> handle(GetAllPlansQuery query) {
        PageResult<dev.studentpp1.streamingservice.subscription.domain.model.SubscriptionPlan> result =
                subscriptionPlanReadRepository.findAll(query.search(), query.page(), query.size());

        List<SubscriptionPlanSummaryReadModel> content = result.content().stream()
                .map(plan -> new SubscriptionPlanSummaryReadModel(
                        plan.getId(),
                        plan.getName(),
                        plan.getDescription(),
                        plan.getPrice(),
                        plan.getDuration(),
                        plan.getVersion()
                ))
                .toList();

        return new PageResult<>(
                content,
                result.page(),
                result.size(),
                result.totalElements(),
                result.totalPages()
        );
    }

    public SubscriptionPlanDetailsReadModel handle(GetPlanByIdQuery query) {
        SubscriptionPlanWithMoviesReadModel result = subscriptionPlanReadRepository.findById(query.id())
                .orElseThrow(() -> new SubscriptionPlanNotFoundException(query.id()));

        List<SubscriptionPlanMovieReadModel> movies = result.movies().stream()
                .map(movie -> new SubscriptionPlanMovieReadModel(
                        movie.id(),
                        movie.title(),
                        movie.description(),
                        movie.year(),
                        movie.rating()
                ))
                .toList();

        return new SubscriptionPlanDetailsReadModel(
                result.plan().getId(),
                result.plan().getName(),
                result.plan().getDescription(),
                result.plan().getPrice(),
                result.plan().getDuration(),
                movies,
                result.plan().getVersion()
        );
    }
}

