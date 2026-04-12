package dev.studentpp1.streamingservice.movies.application.command.actor;

import dev.studentpp1.streamingservice.movies.domain.exception.ActorNotFoundException;
import dev.studentpp1.streamingservice.movies.domain.model.Actor;
import dev.studentpp1.streamingservice.movies.domain.repository.ActorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UpdateActorHandler {
    private final ActorRepository actorRepository;

    public void handle(UpdateActorCommand command) {
        Actor actor = actorRepository.findById(command.id())
                .orElseThrow(() -> new ActorNotFoundException(command.id()));
        actor.update(
                command.request().name(),
                command.request().surname(),
                command.request().biography()
        );
        actorRepository.save(actor);
    }
}
