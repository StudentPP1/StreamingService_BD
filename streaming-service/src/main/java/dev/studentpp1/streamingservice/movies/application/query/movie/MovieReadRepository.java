package dev.studentpp1.streamingservice.movies.application.query.movie;

import dev.studentpp1.streamingservice.common.dto.PageResult;

import java.util.Optional;

public interface MovieReadRepository {

    PageResult<MovieReadModel> findAll(int page, int size);

    Optional<MovieReadModel> findById(Long id);

    Optional<MovieDetailsReadModel> findDetailsById(Long id);
}
