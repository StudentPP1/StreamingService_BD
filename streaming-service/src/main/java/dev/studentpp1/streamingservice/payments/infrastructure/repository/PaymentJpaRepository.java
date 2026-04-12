package dev.studentpp1.streamingservice.payments.infrastructure.repository;

import dev.studentpp1.streamingservice.payments.domain.model.PaymentStatus;
import dev.studentpp1.streamingservice.payments.infrastructure.entity.PaymentEntity;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

public interface PaymentJpaRepository extends JpaRepository<PaymentEntity, Long> {

    // Stripe can send retry with the same intent ->
    // implement pessimistic lock to avoid duplicate of paymentEntity & subscription
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select p from PaymentEntity p where p.providerSessionId = :providerSessionId")
    Optional<PaymentEntity> findByProviderPaymentIdForUpdate(
            @Param("providerSessionId") String providerSessionId);
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Transactional
    @Query("delete from PaymentEntity p where p.status = :status and p.createdAt < :threshold")
    int deleteByStatusAndCreatedAtBefore(@Param("status") PaymentStatus status,
                                         @Param("threshold") LocalDateTime threshold);

    java.util.List<PaymentEntity> findAllByUserId(Long userId);

    java.util.List<PaymentEntity> findAllByUserIdAndUserSubscriptionId(Long userId, Long userSubscriptionId);

    // Modifying: bulk-delete -> already in db (SQL)
    // clearAutomatically -> clear 1st level cache in current Transaction (delete old payments)
    // flushAutomatically -> flush all insert/update in queue before delete
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Transactional
    @Query("delete from PaymentEntity p where p.paidAt < :dateTime")
    int deletePaymentsBefore(@Param("dateTime") LocalDateTime dateTime);

    Optional<PaymentEntity> findByProviderSessionId(String sessionId);
}
