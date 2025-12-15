package dev.studentpp1.streamingservice.subscription.controller;

import dev.studentpp1.streamingservice.auth.persistence.AuthenticatedUser;
import dev.studentpp1.streamingservice.common.validation.ValidId;
import dev.studentpp1.streamingservice.payments.dto.PaymentResponse;
import dev.studentpp1.streamingservice.subscription.dto.request.CreateFamilySubscriptionRequest;
import dev.studentpp1.streamingservice.subscription.dto.request.SubscribeRequest;
import dev.studentpp1.streamingservice.subscription.dto.response.UserSubscriptionDto;
import dev.studentpp1.streamingservice.subscription.mapper.UserSubscriptionMapper;
import dev.studentpp1.streamingservice.subscription.service.SubscriptionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@PreAuthorize("isAuthenticated()")
@RestController
@RequestMapping("/api/subscriptions")
@RequiredArgsConstructor
@Validated
public class SubscriptionController {

    private final SubscriptionService subscriptionService;
    private final UserSubscriptionMapper userSubscriptionMapper;

    @PostMapping
    public ResponseEntity<PaymentResponse> subscribe(
        @Valid @RequestBody SubscribeRequest request,
        @AuthenticationPrincipal AuthenticatedUser currentUser
    ) {
        PaymentResponse paymentResponse = subscriptionService.subscribeUser(request, currentUser);

        return ResponseEntity.status(HttpStatus.CREATED).body(paymentResponse);
    }

    @PostMapping("/family")
    public ResponseEntity<PaymentResponse> createFamilySubscription(
        @Valid @RequestBody CreateFamilySubscriptionRequest request,
        @AuthenticationPrincipal AuthenticatedUser currentUser
    ) {
        PaymentResponse paymentResponse = subscriptionService.createFamilySubscription(request, currentUser);

        return ResponseEntity.status(HttpStatus.CREATED).body(paymentResponse);
    }

    @GetMapping
    public ResponseEntity<Page<UserSubscriptionDto>> getMySubscriptions(
        @AuthenticationPrincipal AuthenticatedUser currentUser,
        @PageableDefault(sort = "endTime", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        var page = subscriptionService.getUserSubscriptions(currentUser, pageable);
        var dtoPage = page.map(userSubscriptionMapper::toDto);

        return ResponseEntity.ok(dtoPage);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> cancelSubscription(
        @PathVariable("id") @ValidId Long subscriptionId,
        @AuthenticationPrincipal AuthenticatedUser currentUser
    ) {
        subscriptionService.cancelSubscription(subscriptionId, currentUser);

        return ResponseEntity.noContent().build();
    }
}
