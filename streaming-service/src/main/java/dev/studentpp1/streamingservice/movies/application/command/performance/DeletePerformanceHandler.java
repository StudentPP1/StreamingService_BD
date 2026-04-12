package dev.studentpp1.streamingservice.movies.application.command.performance;

import dev.studentpp1.streamingservice.movies.domain.exception.PerformanceNotFoundException;
import dev.studentpp1.streamingservice.movies.domain.repository.PerformanceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DeletePerformanceHandler {
    private final PerformanceRepository performanceRepository;

    public void handle(DeletePerformanceCommand command) {
        if (!performanceRepository.existsById(command.id())) {
            throw new PerformanceNotFoundException(command.id());
        }
        performanceRepository.deleteById(command.id());
    }
}
