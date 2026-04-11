package dev.studentpp1.streamingservice.users.infrastructure.mapper;

import dev.studentpp1.streamingservice.users.domain.model.User;
import dev.studentpp1.streamingservice.users.domain.model.vo.Email;
import dev.studentpp1.streamingservice.users.domain.model.vo.HashedPassword;
import dev.studentpp1.streamingservice.users.infrastructure.entity.UserEntity;
import org.springframework.stereotype.Component;

@Component
public class UserPersistenceMapper {

    public User toDomain(UserEntity entity) {
        return User.restore(
                entity.getId(),
                entity.getName(),
                entity.getSurname(),
                new Email(entity.getEmail()),
                new HashedPassword(entity.getPassword()),
                entity.getBirthday(),
                entity.getRole(),
                entity.isDeleted()
        );
    }

    public UserEntity toEntity(User domain) {
        return UserEntity.builder()
                .id(domain.getId())
                .name(domain.getName())
                .surname(domain.getSurname())
                .email(domain.getEmail())
                .password(domain.getPassword())
                .birthday(domain.getBirthday())
                .role(domain.getRole())
                .deleted(domain.isDeleted())
                .build();
    }
}