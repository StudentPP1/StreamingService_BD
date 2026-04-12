package dev.studentpp1.streamingservice.movies.application.command.performance;

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
