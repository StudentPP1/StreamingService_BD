package dev.studentpp1.streamingservice.payments.presentation.controller;

import dev.studentpp1.streamingservice.auth.persistence.AuthenticatedUser;
import dev.studentpp1.streamingservice.payments.application.usecase.PaymentService;
import dev.studentpp1.streamingservice.payments.application.dto.HistoryPaymentResponse;
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

    private final PaymentService paymentService;

    @GetMapping("/user")
    public ResponseEntity<List<HistoryPaymentResponse>> getPaymentsByUser(@AuthenticationPrincipal AuthenticatedUser user) {
        return ResponseEntity.ok(paymentService.getUserPayments(user.getId()));
    }

    @GetMapping("/user/subscription/{id}")
    public ResponseEntity<List<HistoryPaymentResponse>> getPaymentsByUserSubscription(
            @AuthenticationPrincipal AuthenticatedUser user,
            @PathVariable Long id) {
        return ResponseEntity.ok(paymentService.getPaymentsBySubscription(user.getId(), id));
    }
}