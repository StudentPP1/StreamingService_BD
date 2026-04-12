package dev.studentpp1.streamingservice.movies.application.command.performance;

import dev.studentpp1.streamingservice.movies.domain.factory.PerformanceFactory;
import dev.studentpp1.streamingservice.movies.domain.model.Performance;
import dev.studentpp1.streamingservice.movies.domain.repository.PerformanceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CreatePerformanceHandler {
    private final PerformanceRepository performanceRepository;
    private final PerformanceFactory performanceFactory;

    public void handle(CreatePerformanceCommand command) {
        Performance performance = performanceFactory.create(
                command.movieId(),
                command.actorId(),
                command.characterName(),
                command.description()
        );
        performanceRepository.save(performance);
    }
}
