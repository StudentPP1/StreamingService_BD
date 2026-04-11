package dev.studentpp1.streamingservice.subscription.presentation.controller;

import dev.studentpp1.streamingservice.common.dto.PageResult;
import dev.studentpp1.streamingservice.subscription.application.usecase.SubscriptionPlanService;
import dev.studentpp1.streamingservice.subscription.application.usecase.SubscriptionPlanService.PlanWithMovies;
import dev.studentpp1.streamingservice.subscription.presentation.dto.request.CreateSubscriptionPlanRequest;
import dev.studentpp1.streamingservice.subscription.presentation.dto.response.SubscriptionPlanDetailsDto;
import dev.studentpp1.streamingservice.subscription.presentation.dto.response.SubscriptionPlanSummaryDto;
import dev.studentpp1.streamingservice.subscription.presentation.mapper.SubscriptionPlanPresentationMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/subscription-plans")
@RequiredArgsConstructor
@Validated
public class SubscriptionPlanController {

    private final SubscriptionPlanService subscriptionPlanService;
    private final SubscriptionPlanPresentationMapper mapper;

    @GetMapping
    public ResponseEntity<PageResult<SubscriptionPlanSummaryDto>> getAllPlans(
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        var plans = subscriptionPlanService.getAllPlans(search, page, size);

        List<SubscriptionPlanSummaryDto> content = plans.content().stream()
                .map(mapper::toSummaryDto)
                .toList();

        return ResponseEntity.ok(new PageResult<>(
                content, plans.page(), plans.size(),
                plans.totalElements(), plans.totalPages()
        ));
    }

    @GetMapping("/{id}")
    public ResponseEntity<SubscriptionPlanDetailsDto> getPlan(@PathVariable Long id) {
        PlanWithMovies planWithMovies = subscriptionPlanService.getPlanById(id);
        return ResponseEntity.ok(mapper.toDetailsDto(planWithMovies.plan(), planWithMovies.movies()));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<SubscriptionPlanDetailsDto> createPlan(
            @Valid @RequestBody CreateSubscriptionPlanRequest request) {

        PlanWithMovies created = subscriptionPlanService.createPlan(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(mapper.toDetailsDto(created.plan(), created.movies()));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<SubscriptionPlanDetailsDto> updatePlan(
            @PathVariable Long id,
            @Valid @RequestBody CreateSubscriptionPlanRequest request) {

        PlanWithMovies updated = subscriptionPlanService.updatePlan(id, request);
        return ResponseEntity.ok(mapper.toDetailsDto(updated.plan(), updated.movies()));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{id}/movies/add")
    public ResponseEntity<SubscriptionPlanDetailsDto> addMovies(
            @PathVariable Long id,
            @RequestBody List<Long> movieIds) {

        PlanWithMovies updated = subscriptionPlanService.addMoviesToPlan(id, movieIds);
        return ResponseEntity.ok(mapper.toDetailsDto(updated.plan(), updated.movies()));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{id}/movies/remove")
    public ResponseEntity<SubscriptionPlanDetailsDto> removeMovies(
            @PathVariable Long id,
            @RequestBody List<Long> movieIds) {

        PlanWithMovies updated = subscriptionPlanService.removeMoviesFromPlan(id, movieIds);
        return ResponseEntity.ok(mapper.toDetailsDto(updated.plan(), updated.movies()));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePlan(@PathVariable Long id) {
        subscriptionPlanService.deletePlan(id);
        return ResponseEntity.noContent().build();
    }
}