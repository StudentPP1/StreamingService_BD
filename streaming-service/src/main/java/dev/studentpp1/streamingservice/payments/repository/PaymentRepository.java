package dev.studentpp1.streamingservice.payments.repository;

import dev.studentpp1.streamingservice.payments.dto.HistoryPaymentResponse;
import dev.studentpp1.streamingservice.payments.entity.Payment;
import dev.studentpp1.streamingservice.payments.entity.PaymentStatus;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

    // Stripe can send retry with the same intent ->
    // implement pessimistic lock to avoid duplicate of payment & subscription
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select p from Payment p where p.providerSessionId = :providerSessionId")
    Optional<Payment> findByProviderPaymentIdForUpdate(@Param("providerSessionId") String providerSessionId);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Transactional
    @Query("delete from Payment p where p.status = :status and p.createdAt < :threshold")
    int deleteByStatusAndCreatedAtBefore(@Param("status") PaymentStatus status,
                                         @Param("threshold") LocalDateTime threshold);

    @Query("""
            select new dev.studentpp1.streamingservice.payments.dto.HistoryPaymentResponse(
                p.status,
                p.paidAt,
                p.amount,
                sp.name
            )
            from Payment p
            join p.userSubscription us
            join us.plan sp
            join us.user u
            where u.id = :userId
            """)
    List<HistoryPaymentResponse> getPaymentByUserId(Long userId);

    @Query("""
            select new dev.studentpp1.streamingservice.payments.dto.HistoryPaymentResponse(
                p.status,
                p.paidAt,
                p.amount,
                sp.name
            )
            from Payment p
            join p.userSubscription us
            join us.plan sp
            where us.id = :userSubscriptionId
            """)
    List<HistoryPaymentResponse> getPaymentByUserSubscription(Long userSubscriptionId);

    // Modifying: bulk-delete -> already in db (SQL)
    // clearAutomatically -> clear 1st level cache in current Transaction (delete old payments)
    // flushAutomatically -> flush all insert/update in queue before delete
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Transactional
    @Query("delete from Payment p where p.paidAt < :dateTime")
    int deletePaymentsBefore(@Param("dateTime") LocalDateTime dateTime);
}
