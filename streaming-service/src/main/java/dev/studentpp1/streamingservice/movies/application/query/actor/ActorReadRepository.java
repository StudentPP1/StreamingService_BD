package dev.studentpp1.streamingservice.movies.application.query.actor;

import dev.studentpp1.streamingservice.common.dto.PageResult;

import java.util.Optional;

public interface ActorReadRepository {

    PageResult<ActorReadModel> findAll(int page, int size);

    Optional<ActorReadModel> findById(Long id);

    Optional<ActorDetailsReadModel> findDetailsById(Long id);
}
