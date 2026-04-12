package dev.studentpp1.streamingservice.movies.application.command;

import dev.studentpp1.streamingservice.movies.application.usecase.DirectorService;
import dev.studentpp1.streamingservice.movies.domain.model.Director;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DirectorCommandHandler {

    private final DirectorService directorService;

    public Director handle(CreateDirectorCommand command) {
        return directorService.createDirector(command.request());
    }

    public Director handle(UpdateDirectorCommand command) {
        return directorService.updateDirector(command.id(), command.request());
    }

    public void handle(DeleteDirectorCommand command) {
        directorService.deleteDirector(command.id());
    }
}

