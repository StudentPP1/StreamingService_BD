package dev.studentpp1.streamingservice.subscription.application.command;

import dev.studentpp1.streamingservice.subscription.application.usecase.SubscriptionPlanService;
import dev.studentpp1.streamingservice.subscription.application.usecase.SubscriptionPlanService.PlanWithMovies;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SubscriptionPlanCommandHandler {

    private final SubscriptionPlanService subscriptionPlanService;

    public PlanWithMovies handle(CreatePlanCommand command) {
        return subscriptionPlanService.createPlan(command.request());
    }

    public PlanWithMovies handle(UpdatePlanCommand command) {
        return subscriptionPlanService.updatePlan(command.id(), command.request());
    }

    public PlanWithMovies handle(AddMoviesToPlanCommand command) {
        return subscriptionPlanService.addMoviesToPlan(command.id(), command.movieIds());
    }

    public PlanWithMovies handle(RemoveMoviesFromPlanCommand command) {
        return subscriptionPlanService.removeMoviesFromPlan(command.id(), command.movieIds());
    }

    public void handle(DeletePlanCommand command) {
        subscriptionPlanService.deletePlan(command.id());
    }
}

