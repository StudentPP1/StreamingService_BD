package dev.studentpp1.streamingservice.users.domain.repository;

import dev.studentpp1.streamingservice.users.domain.model.User;

import java.util.Optional;

public interface UserRepository {
    Optional<User> findById(Long id);
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
    User save(User user);
    void deleteById(Long id);
}