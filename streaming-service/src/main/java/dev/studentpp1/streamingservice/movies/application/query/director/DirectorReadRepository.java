package dev.studentpp1.streamingservice.movies.application.query.director;

import dev.studentpp1.streamingservice.common.dto.PageResult;

import java.util.Optional;

public interface DirectorReadRepository {

    PageResult<DirectorReadModel> findAll(int page, int size);

    Optional<DirectorReadModel> findById(Long id);

    Optional<DirectorDetailsReadModel> findDetailsById(Long id);
}
