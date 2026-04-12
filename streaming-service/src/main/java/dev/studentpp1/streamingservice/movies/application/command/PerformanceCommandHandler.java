package dev.studentpp1.streamingservice.movies.application.command;

import dev.studentpp1.streamingservice.movies.application.command.performance.CreatePerformanceCommand;
import dev.studentpp1.streamingservice.movies.application.command.performance.CreatePerformanceHandler;
import dev.studentpp1.streamingservice.movies.application.command.performance.DeletePerformanceCommand;
import dev.studentpp1.streamingservice.movies.application.command.performance.DeletePerformanceHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PerformanceCommandHandler {
    private final CreatePerformanceHandler createPerformanceHandler;
    private final DeletePerformanceHandler deletePerformanceHandler;

    public void handle(CreatePerformanceCommand command) {
        createPerformanceHandler.handle(command);
    }

    public void handle(DeletePerformanceCommand command) {
        deletePerformanceHandler.handle(command);
    }
}
