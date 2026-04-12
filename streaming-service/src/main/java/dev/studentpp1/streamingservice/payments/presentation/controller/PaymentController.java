package dev.studentpp1.streamingservice.payments.presentation.controller;

import dev.studentpp1.streamingservice.auth.persistence.AuthenticatedUser;
import dev.studentpp1.streamingservice.payments.application.dto.HistoryPaymentResponse;
import dev.studentpp1.streamingservice.payments.application.query.GetSubscriptionPaymentsQuery;
import dev.studentpp1.streamingservice.payments.application.query.GetUserPaymentsQuery;
import dev.studentpp1.streamingservice.payments.application.query.PaymentQueryHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@PreAuthorize("isAuthenticated()")
@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentQueryHandler paymentQueryHandler;

    @GetMapping("/user")
    public ResponseEntity<List<HistoryPaymentResponse>> getPaymentsByUser(@AuthenticationPrincipal AuthenticatedUser user) {
        return ResponseEntity.ok(paymentQueryHandler.handle(new GetUserPaymentsQuery(user.getId())));
    }

    @GetMapping("/user/subscription/{id}")
    public ResponseEntity<List<HistoryPaymentResponse>> getPaymentsByUserSubscription(
            @AuthenticationPrincipal AuthenticatedUser user,
            @PathVariable Long id) {
        return ResponseEntity.ok(paymentQueryHandler.handle(new GetSubscriptionPaymentsQuery(user.getId(), id)));
    }
}