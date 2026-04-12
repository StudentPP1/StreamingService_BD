package dev.studentpp1.streamingservice.movies.application.command.director;

import dev.studentpp1.streamingservice.movies.domain.exception.DirectorNotFoundException;
import dev.studentpp1.streamingservice.movies.domain.model.Director;
import dev.studentpp1.streamingservice.movies.domain.repository.DirectorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UpdateDirectorHandler {
    private final DirectorRepository directorRepository;

    public void handle(UpdateDirectorCommand command) {
        Director director = directorRepository.findById(command.id())
                .orElseThrow(() -> new DirectorNotFoundException(command.id()));
        director.update(
                command.request().name(),
                command.request().surname(),
                command.request().biography()
        );
        directorRepository.save(director);
    }
}
