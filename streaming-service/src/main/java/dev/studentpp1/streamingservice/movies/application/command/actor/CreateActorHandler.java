package dev.studentpp1.streamingservice.movies.application.command.actor;

import dev.studentpp1.streamingservice.movies.domain.factory.ActorFactory;
import dev.studentpp1.streamingservice.movies.domain.model.Actor;
import dev.studentpp1.streamingservice.movies.domain.repository.ActorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CreateActorHandler {
    private final ActorRepository actorRepository;
    private final ActorFactory actorFactory;

    public void handle(CreateActorCommand command) {
        Actor actor = actorFactory.create(
                command.name(),
                command.surname(),
                command.biography()
        );
        actorRepository.save(actor);
    }
}
