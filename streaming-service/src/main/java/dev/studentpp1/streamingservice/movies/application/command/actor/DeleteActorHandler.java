package dev.studentpp1.streamingservice.movies.application.command.actor;

import dev.studentpp1.streamingservice.movies.domain.exception.ActorNotFoundException;
import dev.studentpp1.streamingservice.movies.domain.repository.ActorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DeleteActorHandler {
    private final ActorRepository actorRepository;

    public void handle(DeleteActorCommand command) {
        if (!actorRepository.existsById(command.id())) {
            throw new ActorNotFoundException(command.id());
        }
        actorRepository.deleteById(command.id());
    }
}
