package dev.studentpp1.streamingservice.movies.application.command;

import dev.studentpp1.streamingservice.movies.application.usecase.ActorService;
import dev.studentpp1.streamingservice.movies.domain.model.Actor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ActorCommandHandler {

    private final ActorService actorService;

    public Actor handle(CreateActorCommand command) {
        return actorService.createActor(command.request());
    }

    public Actor handle(UpdateActorCommand command) {
        return actorService.updateActor(command.id(), command.request());
    }

    public void handle(DeleteActorCommand command) {
        actorService.deleteActor(command.id());
    }
}

