package dev.studentpp1.streamingservice.subscription.application.command.plan;

import dev.studentpp1.streamingservice.subscription.domain.factory.SubscriptionPlanFactory;
import dev.studentpp1.streamingservice.subscription.domain.model.SubscriptionMovie;
import dev.studentpp1.streamingservice.subscription.domain.model.SubscriptionPlan;
import dev.studentpp1.streamingservice.subscription.domain.port.MovieProvider;
import dev.studentpp1.streamingservice.subscription.domain.repository.SubscriptionPlanRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class CreatePlanHandler {

    private final SubscriptionPlanRepository subscriptionPlanRepository;
    private final SubscriptionPlanFactory subscriptionPlanFactory;
    private final SubscriptionPlanMovieValidator movieValidator;
    private final MovieProvider movieProvider;

    @Transactional
    public void handle(CreatePlanCommand command) {
        SubscriptionPlan plan = subscriptionPlanFactory.create(
                command.name(),
                command.description(),
                command.price(),
                command.duration()
        );

        List<SubscriptionMovie> movies = List.of();
        if (command.includedMovieIds() != null && !command.includedMovieIds().isEmpty()) {
            Set<Long> movieIds = movieValidator.validateAndGetMovieIds(command.includedMovieIds());
            plan.setMovieIds(movieIds);
            movies = movieProvider.findAllById(new ArrayList<>(movieIds));
        }

        subscriptionPlanRepository.save(plan);
    }
}

