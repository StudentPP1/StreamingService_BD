package dev.studentpp1.streamingservice.movies.application.query.performance;

import dev.studentpp1.streamingservice.movies.domain.exception.PerformanceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PerformanceQueryHandler {

    private final PerformanceReadRepository performanceReadRepository;

    public PerformanceReadModel handle(GetPerformanceByIdQuery query) {
        return performanceReadRepository.findById(query.id())
                .orElseThrow(() -> new PerformanceNotFoundException(query.id()));
    }
}
