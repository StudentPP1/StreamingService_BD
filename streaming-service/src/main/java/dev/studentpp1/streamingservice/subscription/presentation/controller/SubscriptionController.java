package dev.studentpp1.streamingservice.subscription.presentation.controller;

import dev.studentpp1.streamingservice.auth.persistence.AuthenticatedUser;
import dev.studentpp1.streamingservice.common.dto.PageResult;
import dev.studentpp1.streamingservice.subscription.application.usecase.SubscriptionService;
import dev.studentpp1.streamingservice.subscription.application.usecase.SubscriptionService.UserSubscriptionWithPlan;
import dev.studentpp1.streamingservice.subscription.domain.model.CheckoutResult;
import dev.studentpp1.streamingservice.subscription.application.dto.CreateFamilySubscriptionRequest;
import dev.studentpp1.streamingservice.subscription.application.dto.SubscribeRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/subscriptions")
@RequiredArgsConstructor
@Validated
@PreAuthorize("isAuthenticated()")
public class SubscriptionController {

    private final SubscriptionService subscriptionService;

    @PostMapping("/subscribe")
    public ResponseEntity<CheckoutResult> subscribe(
            @Valid @RequestBody SubscribeRequest request,
            @AuthenticationPrincipal AuthenticatedUser currentUser) {
        CheckoutResult result = subscriptionService.subscribeUser(request, currentUser.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    @PostMapping("/family")
    public ResponseEntity<CheckoutResult> subscribeFamily(
            @Valid @RequestBody CreateFamilySubscriptionRequest request,
            @AuthenticationPrincipal AuthenticatedUser currentUser) {
        CheckoutResult result = subscriptionService.createFamilySubscription(request, currentUser.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    @GetMapping("/my")
    public ResponseEntity<PageResult<UserSubscriptionWithPlan>> getMySubscriptions(
            @AuthenticationPrincipal AuthenticatedUser currentUser,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(
                subscriptionService.getUserSubscriptionsWithPlan(currentUser.getId(), page, size)
        );
    }

    @PatchMapping("/{id}/cancel")
    public ResponseEntity<Void> cancelSubscription(
            @PathVariable("id") Long subscriptionId,
            @AuthenticationPrincipal AuthenticatedUser currentUser) {
        subscriptionService.cancelSubscription(subscriptionId, currentUser.getId());
        return ResponseEntity.noContent().build();
    }
}