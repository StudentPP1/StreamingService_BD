package dev.studentpp1.streamingservice.payments.presentation.controller;

import dev.studentpp1.streamingservice.auth.persistence.AuthenticatedUser;
import dev.studentpp1.streamingservice.payments.application.cqs.PaymentQueryHandler;
import dev.studentpp1.streamingservice.payments.application.cqs.PaymentsCqs.GetPaymentsBySubscriptionQuery;
import dev.studentpp1.streamingservice.payments.application.cqs.PaymentsCqs.GetUserPaymentsQuery;
import dev.studentpp1.streamingservice.payments.presentation.dto.HistoryPaymentResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@PreAuthorize("isAuthenticated()")
@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentQueryHandler paymentQueryHandler;

    @GetMapping("/user")
    public ResponseEntity<java.util.List<HistoryPaymentResponse>> getPaymentsByUser(@AuthenticationPrincipal AuthenticatedUser user) {
        return ResponseEntity.ok(paymentQueryHandler.handle(new GetUserPaymentsQuery(user.getId())));
    }

    @GetMapping("/user/subscription/{id}")
    public ResponseEntity<java.util.List<HistoryPaymentResponse>> getPaymentsByUserSubscription(
            @AuthenticationPrincipal AuthenticatedUser user,
            @PathVariable Long id) {
        return ResponseEntity.ok(paymentQueryHandler.handle(new GetPaymentsBySubscriptionQuery(user.getId(), id)));
    }
}