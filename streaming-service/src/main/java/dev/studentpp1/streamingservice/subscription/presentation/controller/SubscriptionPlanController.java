package dev.studentpp1.streamingservice.subscription.presentation.controller;

import dev.studentpp1.streamingservice.subscription.application.cqs.SubscriptionCqs.*;
import dev.studentpp1.streamingservice.subscription.application.cqs.SubscriptionCommandHandler;
import dev.studentpp1.streamingservice.subscription.application.cqs.SubscriptionQueryHandler;
import dev.studentpp1.streamingservice.subscription.presentation.dto.request.CreateSubscriptionPlanRequest;
import dev.studentpp1.streamingservice.subscription.presentation.dto.response.SubscriptionPlanDetailsDto;
import dev.studentpp1.streamingservice.subscription.presentation.dto.response.SubscriptionPlanSummaryDto;
import dev.studentpp1.streamingservice.common.dto.PageResult;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/subscription-plans")
@RequiredArgsConstructor
@Validated
public class SubscriptionPlanController {

    private final SubscriptionCommandHandler subscriptionCommandHandler;
    private final SubscriptionQueryHandler subscriptionQueryHandler;

    @GetMapping
    public ResponseEntity<PageResult<SubscriptionPlanSummaryDto>> getAllPlans(
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(subscriptionQueryHandler.handle(new GetAllPlansQuery(search, page, size)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<SubscriptionPlanDetailsDto> getPlan(@PathVariable Long id) {
        return ResponseEntity.ok(subscriptionQueryHandler.handle(new GetPlanByIdQuery(id)));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<SubscriptionPlanDetailsDto> createPlan(
            @Valid @RequestBody CreateSubscriptionPlanRequest request) {
        Long id = subscriptionCommandHandler.handle(new CreatePlanCommand(request));
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(subscriptionQueryHandler.handle(new GetPlanByIdQuery(id)));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<SubscriptionPlanDetailsDto> updatePlan(
            @PathVariable Long id,
            @Valid @RequestBody CreateSubscriptionPlanRequest request) {
        subscriptionCommandHandler.handle(new UpdatePlanCommand(id, request));
        return ResponseEntity.ok(subscriptionQueryHandler.handle(new GetPlanByIdQuery(id)));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{id}/movies/add")
    public ResponseEntity<SubscriptionPlanDetailsDto> addMovies(
            @PathVariable Long id,
            @RequestBody java.util.List<Long> movieIds) {
        subscriptionCommandHandler.handle(new AddMoviesToPlanCommand(id, movieIds));
        return ResponseEntity.ok(subscriptionQueryHandler.handle(new GetPlanByIdQuery(id)));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{id}/movies/remove")
    public ResponseEntity<SubscriptionPlanDetailsDto> removeMovies(
            @PathVariable Long id,
            @RequestBody java.util.List<Long> movieIds) {
        subscriptionCommandHandler.handle(new RemoveMoviesFromPlanCommand(id, movieIds));
        return ResponseEntity.ok(subscriptionQueryHandler.handle(new GetPlanByIdQuery(id)));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePlan(@PathVariable Long id) {
        subscriptionCommandHandler.handle(new DeletePlanCommand(id));
        return ResponseEntity.noContent().build();
    }
}