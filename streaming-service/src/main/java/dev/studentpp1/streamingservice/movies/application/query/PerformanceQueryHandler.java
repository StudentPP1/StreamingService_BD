package dev.studentpp1.streamingservice.movies.application.query;

import dev.studentpp1.streamingservice.movies.application.usecase.PerformanceService;
import dev.studentpp1.streamingservice.movies.domain.model.Performance;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PerformanceQueryHandler {

    private final PerformanceService performanceService;

    public Performance handle(GetPerformanceByIdQuery query) {
        return performanceService.getPerformanceById(query.id());
    }
}

