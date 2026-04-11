package dev.studentpp1.streamingservice.movies.domain.repository;

import dev.studentpp1.streamingservice.movies.domain.model.Director;
import dev.studentpp1.streamingservice.common.dto.PageResult;

import java.util.List;
import java.util.Optional;

public interface DirectorRepository {
    Optional<Director> findById(Long id);
    List<Director> findAll();
    Director save(Director director);
    boolean existsById(Long id);
    void deleteById(Long id);
    PageResult<Director> findAll(int page, int size);
}