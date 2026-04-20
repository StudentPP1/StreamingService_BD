package dev.studentpp1.streamingservice.users.domain.port;

import dev.studentpp1.streamingservice.users.domain.model.User;

import java.util.Optional;

public interface UserQueryPort {
    Optional<User> findById(Long id);
    Optional<User> findByEmail(String email);
}
