package dev.studentpp1.streamingservice.users.application.query.repo;

import dev.studentpp1.streamingservice.users.application.query.readmodel.UserReadModel;

import java.util.Optional;

public interface UserReadRepository {
    Optional<UserReadModel> findById(Long id);

    Optional<UserReadModel> findByEmail(String email);
}

