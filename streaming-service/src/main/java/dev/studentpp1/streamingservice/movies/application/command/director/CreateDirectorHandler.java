package dev.studentpp1.streamingservice.movies.application.command.director;

import dev.studentpp1.streamingservice.movies.domain.factory.DirectorFactory;
import dev.studentpp1.streamingservice.movies.domain.model.Director;
import dev.studentpp1.streamingservice.movies.domain.repository.DirectorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CreateDirectorHandler {
    private final DirectorRepository directorRepository;
    private final DirectorFactory directorFactory;

    public void handle(CreateDirectorCommand command) {
        Director director = directorFactory.create(
                command.name(),
                command.surname(),
                command.biography()
        );
        directorRepository.save(director);
    }
}
