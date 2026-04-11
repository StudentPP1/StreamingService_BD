package dev.studentpp1.streamingservice.users.infrastructure.adapter;

import dev.studentpp1.streamingservice.users.domain.model.User;
import dev.studentpp1.streamingservice.users.domain.repository.UserRepository;
import dev.studentpp1.streamingservice.users.infrastructure.mapper.UserPersistenceMapper;
import dev.studentpp1.streamingservice.users.infrastructure.repository.UserJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class UserRepositoryAdapter implements UserRepository {

    private final UserJpaRepository jpaRepository;
    private final UserPersistenceMapper mapper;

    @Override
    public Optional<User> findById(Long id) {
        return jpaRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return jpaRepository.findByEmail(email).map(mapper::toDomain);
    }

    @Override
    public boolean existsByEmail(String email) {
        return jpaRepository.findByEmail(email).isPresent();
    }

    @Override
    public User save(User user) {
        return mapper.toDomain(jpaRepository.save(mapper.toEntity(user)));
    }

    @Override
    public void deleteById(Long id) {
        jpaRepository.deleteById(id);
    }
}