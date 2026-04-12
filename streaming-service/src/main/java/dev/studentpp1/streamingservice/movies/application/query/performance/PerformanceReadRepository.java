package dev.studentpp1.streamingservice.movies.application.query.performance;

import java.util.Optional;

public interface PerformanceReadRepository {

    Optional<PerformanceReadModel> findById(Long id);
}
