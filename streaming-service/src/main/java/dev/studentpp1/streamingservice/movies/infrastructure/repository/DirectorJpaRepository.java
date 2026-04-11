package dev.studentpp1.streamingservice.movies.infrastructure.repository;

import dev.studentpp1.streamingservice.movies.infrastructure.entity.DirectorEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DirectorJpaRepository extends JpaRepository<DirectorEntity, Long> {
}