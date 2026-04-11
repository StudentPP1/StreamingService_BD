package dev.studentpp1.streamingservice.movies.domain.repository;

import dev.studentpp1.streamingservice.movies.domain.model.Performance;
import java.util.List;
import java.util.Optional;

public interface PerformanceRepository {
    Optional<Performance> findById(Long id);
    List<Performance> findByMovieId(Long movieId);
    List<Performance> findByActorId(Long actorId);
    Performance save(Performance performance);
    void deleteById(Long id);
    boolean existsById(Long id);
}