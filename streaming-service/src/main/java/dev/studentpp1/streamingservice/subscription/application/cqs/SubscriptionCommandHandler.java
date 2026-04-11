package dev.studentpp1.streamingservice.subscription.application.cqs;
import dev.studentpp1.streamingservice.subscription.application.cqs.SubscriptionCqs.*;
import dev.studentpp1.streamingservice.subscription.application.usecase.SubscriptionPlanService;
import dev.studentpp1.streamingservice.subscription.application.usecase.SubscriptionService;
import dev.studentpp1.streamingservice.subscription.domain.model.CheckoutResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
@Service
@RequiredArgsConstructor
public class SubscriptionCommandHandler {
    private final SubscriptionPlanService subscriptionPlanService;
    private final SubscriptionService subscriptionService;
    public Long handle(CreatePlanCommand command) {
        return subscriptionPlanService.createPlan(command.request()).plan().getId();
    }
    public Long handle(UpdatePlanCommand command) {
        return subscriptionPlanService.updatePlan(command.id(), command.request()).plan().getId();
    }
    public Long handle(AddMoviesToPlanCommand command) {
        return subscriptionPlanService.addMoviesToPlan(command.id(), command.movieIds()).plan().getId();
    }
    public Long handle(RemoveMoviesFromPlanCommand command) {
        return subscriptionPlanService.removeMoviesFromPlan(command.id(), command.movieIds()).plan().getId();
    }
    public void handle(DeletePlanCommand command) {
        subscriptionPlanService.deletePlan(command.id());
    }
    public CheckoutResult handle(SubscribeCommand command) {
        return subscriptionService.subscribeUser(command.request(), command.userId());
    }
    public CheckoutResult handle(CreateFamilySubscriptionCommand command) {
        return subscriptionService.createFamilySubscription(command.request(), command.userId());
    }
    public void handle(CancelSubscriptionCommand command) {
        subscriptionService.cancelSubscription(command.subscriptionId(), command.userId());
    }
}
