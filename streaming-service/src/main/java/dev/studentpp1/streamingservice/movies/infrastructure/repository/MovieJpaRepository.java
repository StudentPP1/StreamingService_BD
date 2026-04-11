package dev.studentpp1.streamingservice.movies.infrastructure.repository;

import dev.studentpp1.streamingservice.movies.infrastructure.entity.MovieEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MovieJpaRepository extends JpaRepository<MovieEntity, Long> {
    List<MovieEntity> findAllByDirectorEntityId(Long directorId);
}