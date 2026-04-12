package dev.studentpp1.streamingservice.subscription.application.command.plan;

import dev.studentpp1.streamingservice.subscription.domain.exception.MoviesNotInPlanException;
import dev.studentpp1.streamingservice.subscription.domain.exception.SubscriptionPlanNotFoundException;
import dev.studentpp1.streamingservice.subscription.domain.model.SubscriptionPlan;
import dev.studentpp1.streamingservice.subscription.domain.repository.SubscriptionPlanRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;

@Component
@RequiredArgsConstructor
public class RemoveMoviesFromPlanHandler {

    private final SubscriptionPlanRepository subscriptionPlanRepository;

    @Transactional
    public void handle(RemoveMoviesFromPlanCommand command) {
        SubscriptionPlan plan = subscriptionPlanRepository.findById(command.id())
                .orElseThrow(() -> new SubscriptionPlanNotFoundException(command.id()));

        if (!plan.removeMovies(new HashSet<>(command.movieIds()))) {
            throw new MoviesNotInPlanException();
        }

        subscriptionPlanRepository.save(plan);
    }
}

