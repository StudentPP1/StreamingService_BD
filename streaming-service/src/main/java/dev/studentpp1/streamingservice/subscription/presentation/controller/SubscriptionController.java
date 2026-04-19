package dev.studentpp1.streamingservice.subscription.presentation.controller;

import dev.studentpp1.streamingservice.auth.persistence.AuthenticatedUser;
import dev.studentpp1.streamingservice.common.dto.PageResult;
import dev.studentpp1.streamingservice.subscription.application.command.SubscriptionCommandHandler;
import dev.studentpp1.streamingservice.subscription.application.command.subscription.CancelSubscriptionCommand;
import dev.studentpp1.streamingservice.subscription.application.command.subscription.SubscribeUserCommand;
import dev.studentpp1.streamingservice.subscription.application.query.GetMySubscriptionsQuery;
import dev.studentpp1.streamingservice.subscription.application.query.SubscriptionQueryHandler;
import dev.studentpp1.streamingservice.subscription.application.query.readmodel.UserSubscriptionWithPlanReadModel;
import dev.studentpp1.streamingservice.subscription.domain.model.CheckoutResult;
import dev.studentpp1.streamingservice.subscription.presentation.dto.SubscribeRequest;
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

    private final SubscriptionCommandHandler subscriptionCommandHandler;
    private final SubscriptionQueryHandler subscriptionQueryHandler;

    @PostMapping("/subscribe")
    public ResponseEntity<CheckoutResult> subscribe(
            @Valid @RequestBody SubscribeRequest request,
            @AuthenticationPrincipal AuthenticatedUser currentUser) {
        CheckoutResult result = subscriptionCommandHandler.handle(new SubscribeUserCommand(request.planId(), currentUser.getId(), currentUser.getUsername()));
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    @GetMapping("/my")
    public ResponseEntity<PageResult<UserSubscriptionWithPlanReadModel>> getMySubscriptions(
            @AuthenticationPrincipal AuthenticatedUser currentUser,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(
                subscriptionQueryHandler.handle(new GetMySubscriptionsQuery(currentUser.getId(), page, size))
        );
    }

    @PatchMapping("/{id}/cancel")
    public ResponseEntity<Void> cancelSubscription(
            @PathVariable("id") Long subscriptionId,
            @AuthenticationPrincipal AuthenticatedUser currentUser) {
        subscriptionCommandHandler.handle(new CancelSubscriptionCommand(subscriptionId, currentUser.getId()));
        return ResponseEntity.noContent().build();
    }
}