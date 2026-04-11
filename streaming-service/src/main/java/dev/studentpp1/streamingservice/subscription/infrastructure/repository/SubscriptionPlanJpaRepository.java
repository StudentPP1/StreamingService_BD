package dev.studentpp1.streamingservice.subscription.infrastructure.repository;

import dev.studentpp1.streamingservice.subscription.infrastructure.entity.SubscriptionPlanEntity;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SubscriptionPlanJpaRepository extends JpaRepository<SubscriptionPlanEntity, Long> {
    Optional<SubscriptionPlanEntity> findByName(String name);

    Page<SubscriptionPlanEntity> findAllByNameContainingIgnoreCase(String name, Pageable pageable);

    @EntityGraph(attributePaths = "movieIds")
    Optional<SubscriptionPlanEntity> findWithMoviesById(Long id);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT p FROM SubscriptionPlanEntity p WHERE p.id = :id")
    Optional<SubscriptionPlanEntity> findByIdWithLock(@Param("id") Long id);

    boolean existsByName(String name);
}
