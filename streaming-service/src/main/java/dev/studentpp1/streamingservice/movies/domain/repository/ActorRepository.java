package dev.studentpp1.streamingservice.movies.domain.repository;

import dev.studentpp1.streamingservice.movies.domain.model.Actor;
import dev.studentpp1.streamingservice.common.dto.PageResult;

import java.util.List;
import java.util.Optional;

public interface ActorRepository {
    Optional<Actor> findById(Long id);
    List<Actor> findAll();
    Actor save(Actor actor);
    void deleteById(Long id);
    boolean existsById(Long id);
    PageResult<Actor> findAll(int page, int size);
}