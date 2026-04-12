package dev.studentpp1.streamingservice.movies.application.command;

import dev.studentpp1.streamingservice.movies.application.command.actor.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ActorCommandHandler {
    private final CreateActorHandler createActorHandler;
    private final UpdateActorHandler updateActorHandler;
    private final DeleteActorHandler deleteActorHandler;

    public void handle(CreateActorCommand command) {
        createActorHandler.handle(command);
    }

    public void handle(UpdateActorCommand command) {
        updateActorHandler.handle(command);
    }

    public void handle(DeleteActorCommand command) {
        deleteActorHandler.handle(command);
    }
}
