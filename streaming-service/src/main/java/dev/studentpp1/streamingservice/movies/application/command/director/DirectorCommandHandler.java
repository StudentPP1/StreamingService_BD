package dev.studentpp1.streamingservice.movies.application.command.director;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DirectorCommandHandler {
    private final CreateDirectorHandler createDirectorHandler;
    private final UpdateDirectorHandler updateDirectorHandler;
    private final DeleteDirectorHandler deleteDirectorHandler;

    public void handle(CreateDirectorCommand command) {
        createDirectorHandler.handle(command);
    }

    public void handle(UpdateDirectorCommand command) {
        updateDirectorHandler.handle(command);
    }

    public void handle(DeleteDirectorCommand command) {
        deleteDirectorHandler.handle(command);
    }
}
