package dev.studentpp1.streamingservice.users.infrastructure.adapter;

import dev.studentpp1.streamingservice.users.application.query.readmodel.UserReadModel;
import dev.studentpp1.streamingservice.users.application.query.repo.UserReadRepository;
import dev.studentpp1.streamingservice.users.infrastructure.entity.UserEntity;
import dev.studentpp1.streamingservice.users.infrastructure.repository.UserJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class UserReadRepositoryAdapter implements UserReadRepository {

    private final UserJpaRepository userJpaRepository;

    @Override
    public Optional<UserReadModel> findById(Long id) {
        return userJpaRepository.findById(id).map(this::toReadModel);
    }

    @Override
    public Optional<UserReadModel> findByEmail(String email) {
        return userJpaRepository.findByEmail(email).map(this::toReadModel);
    }

    private UserReadModel toReadModel(UserEntity entity) {
        return new UserReadModel(
                entity.getId(),
                entity.getName(),
                entity.getSurname(),
                entity.getEmail(),
                entity.getBirthday(),
                entity.getRole(),
                entity.isDeleted()
        );
    }
}

