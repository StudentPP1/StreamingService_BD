package dev.studentpp1.streamingservice.users.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import dev.studentpp1.streamingservice.users.entity.AppUser;

public interface UserRepository extends JpaRepository<AppUser, Long> {
    Optional<AppUser> findByEmail(String email);

    Optional<AppUser> findById(@Param("id") Long id);

    // @SQLRestriction don't used for native queries
    @Query(
            value = "SELECT * FROM users WHERE user_id = :id",
            nativeQuery = true
    )
    Optional<AppUser> findByIdIncludingDeleted(@Param("id") Long id);
}
