package dev.studentpp1.streamingservice.movies.infrastructure.repository;

import dev.studentpp1.streamingservice.movies.infrastructure.entity.ActorEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ActorJpaRepository extends JpaRepository<ActorEntity, Long> {
}
