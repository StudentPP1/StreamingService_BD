package dev.studentpp1.streamingservice.movies.infrastructure.repository;

import dev.studentpp1.streamingservice.movies.infrastructure.entity.PerformanceEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PerformanceJpaRepository extends JpaRepository<PerformanceEntity, Long> {
    List<PerformanceEntity> findAllByMovieEntityId(Long movieId);

    List<PerformanceEntity> findAllByActorEntityId(Long actorId);
}