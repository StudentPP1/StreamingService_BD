package dev.studentpp1.streamingservice.subscription.application.command.plan;

import dev.studentpp1.streamingservice.subscription.domain.exception.SubscriptionPlanNotFoundException;
import dev.studentpp1.streamingservice.subscription.domain.model.SubscriptionPlan;
import dev.studentpp1.streamingservice.subscription.domain.repository.SubscriptionPlanRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class AddMoviesToPlanHandler {

    private final SubscriptionPlanRepository subscriptionPlanRepository;
    private final SubscriptionPlanMovieValidator movieValidator;

    @Transactional
    public void handle(AddMoviesToPlanCommand command) {
        SubscriptionPlan plan = subscriptionPlanRepository.findById(command.id())
                .orElseThrow(() -> new SubscriptionPlanNotFoundException(command.id()));

        plan.addMovies(movieValidator.validateAndGetMovieIds(command.movieIds()));
        subscriptionPlanRepository.save(plan);
    }
}

