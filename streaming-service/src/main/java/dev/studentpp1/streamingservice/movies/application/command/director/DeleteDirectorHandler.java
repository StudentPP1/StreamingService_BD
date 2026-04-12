package dev.studentpp1.streamingservice.movies.application.command.director;

import dev.studentpp1.streamingservice.movies.domain.exception.DirectorNotFoundException;
import dev.studentpp1.streamingservice.movies.domain.repository.DirectorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DeleteDirectorHandler {
    private final DirectorRepository directorRepository;

    public void handle(DeleteDirectorCommand command) {
        if (!directorRepository.existsById(command.id())) {
            throw new DirectorNotFoundException(command.id());
        }
        directorRepository.deleteById(command.id());
    }
}
