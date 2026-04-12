package dev.studentpp1.streamingservice.subscription.application.command;

import dev.studentpp1.streamingservice.subscription.application.command.plan.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SubscriptionPlanCommandHandler {

    private final CreatePlanHandler createPlanHandler;
    private final UpdatePlanHandler updatePlanHandler;
    private final AddMoviesToPlanHandler addMoviesToPlanHandler;
    private final RemoveMoviesFromPlanHandler removeMoviesFromPlanHandler;
    private final DeletePlanHandler deletePlanHandler;

    public void handle(CreatePlanCommand command) {
        createPlanHandler.handle(command);
    }

    public void handle(UpdatePlanCommand command) {
        updatePlanHandler.handle(command);
    }

    public void handle(AddMoviesToPlanCommand command) {
        addMoviesToPlanHandler.handle(command);
    }

    public void handle(RemoveMoviesFromPlanCommand command) {
        removeMoviesFromPlanHandler.handle(command);
    }

    public void handle(DeletePlanCommand command) {
        deletePlanHandler.handle(command);
    }
}

