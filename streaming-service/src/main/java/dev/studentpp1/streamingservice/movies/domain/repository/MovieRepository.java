package dev.studentpp1.streamingservice.movies.domain.repository;

import dev.studentpp1.streamingservice.movies.domain.model.Movie;
import dev.studentpp1.streamingservice.common.dto.PageResult;

import java.util.List;
import java.util.Optional;

public interface MovieRepository {
    Optional<Movie> findById(Long id);
    List<Movie> findAll();
    Movie save(Movie movie);
    void deleteById(Long id);
    boolean existsById(Long id);
    PageResult<Movie> findAll(int page, int size);
    List<Movie> findAllByDirectorEntityId(Long directorId);
    List<Movie> findAllById(List<Long> movieIds);
}