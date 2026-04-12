package dev.studentpp1.streamingservice.movies.application.command;

import dev.studentpp1.streamingservice.movies.application.usecase.PerformanceService;
import dev.studentpp1.streamingservice.movies.domain.model.Performance;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PerformanceCommandHandler {

    private final PerformanceService performanceService;

    public Performance handle(CreatePerformanceCommand command) {
        return performanceService.createPerformance(command.request());
    }

    public void handle(DeletePerformanceCommand command) {
        performanceService.deletePerformance(command.id());
    }
}

