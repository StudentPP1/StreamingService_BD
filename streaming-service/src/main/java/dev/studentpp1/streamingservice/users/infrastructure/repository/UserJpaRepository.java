package dev.studentpp1.streamingservice.users.infrastructure.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import dev.studentpp1.streamingservice.users.infrastructure.entity.UserEntity;

public interface UserJpaRepository extends JpaRepository<UserEntity, Long> {
    Optional<UserEntity> findByEmail(String email);

    Optional<UserEntity> findById(@Param("id") Long id);

    // @SQLRestriction don't used for native queries
    @Query(
            value = "SELECT * FROM users WHERE user_id = :id",
            nativeQuery = true
    )
    Optional<UserEntity> findByIdIncludingDeleted(@Param("id") Long id);
}
