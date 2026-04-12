package dev.studentpp1.streamingservice.subscription.presentation.controller;

import dev.studentpp1.streamingservice.common.dto.PageResult;
import dev.studentpp1.streamingservice.subscription.application.command.AddMoviesToPlanCommand;
import dev.studentpp1.streamingservice.subscription.application.command.CreatePlanCommand;
import dev.studentpp1.streamingservice.subscription.application.command.DeletePlanCommand;
import dev.studentpp1.streamingservice.subscription.application.command.RemoveMoviesFromPlanCommand;
import dev.studentpp1.streamingservice.subscription.application.command.SubscriptionPlanCommandHandler;
import dev.studentpp1.streamingservice.subscription.application.command.UpdatePlanCommand;
import dev.studentpp1.streamingservice.subscription.application.query.GetAllPlansQuery;
import dev.studentpp1.streamingservice.subscription.application.query.GetPlanByIdQuery;
import dev.studentpp1.streamingservice.subscription.application.query.SubscriptionPlanQueryHandler;
import dev.studentpp1.streamingservice.subscription.application.dto.CreateSubscriptionPlanRequest;
import dev.studentpp1.streamingservice.subscription.application.usecase.SubscriptionPlanService.PlanWithMovies;
import dev.studentpp1.streamingservice.subscription.presentation.dto.SubscriptionPlanDetailsDto;
import dev.studentpp1.streamingservice.subscription.presentation.dto.SubscriptionPlanSummaryDto;
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

    private final SubscriptionPlanCommandHandler subscriptionPlanCommandHandler;
    private final SubscriptionPlanQueryHandler subscriptionPlanQueryHandler;
    private final SubscriptionPlanPresentationMapper mapper;

    @GetMapping
    public ResponseEntity<PageResult<SubscriptionPlanSummaryDto>> getAllPlans(
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        var plans = subscriptionPlanQueryHandler.handle(new GetAllPlansQuery(search, page, size));

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
        PlanWithMovies planWithMovies = subscriptionPlanQueryHandler.handle(new GetPlanByIdQuery(id));
        return ResponseEntity.ok(mapper.toDetailsDto(planWithMovies.plan(), planWithMovies.movies()));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<SubscriptionPlanDetailsDto> createPlan(
            @Valid @RequestBody CreateSubscriptionPlanRequest request) {

        PlanWithMovies created = subscriptionPlanCommandHandler.handle(new CreatePlanCommand(request));
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(mapper.toDetailsDto(created.plan(), created.movies()));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<SubscriptionPlanDetailsDto> updatePlan(
            @PathVariable Long id,
            @Valid @RequestBody CreateSubscriptionPlanRequest request) {

        PlanWithMovies updated = subscriptionPlanCommandHandler.handle(new UpdatePlanCommand(id, request));
        return ResponseEntity.ok(mapper.toDetailsDto(updated.plan(), updated.movies()));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{id}/movies/add")
    public ResponseEntity<SubscriptionPlanDetailsDto> addMovies(
            @PathVariable Long id,
            @RequestBody List<Long> movieIds) {

        PlanWithMovies updated = subscriptionPlanCommandHandler.handle(new AddMoviesToPlanCommand(id, movieIds));
        return ResponseEntity.ok(mapper.toDetailsDto(updated.plan(), updated.movies()));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{id}/movies/remove")
    public ResponseEntity<SubscriptionPlanDetailsDto> removeMovies(
            @PathVariable Long id,
            @RequestBody List<Long> movieIds) {

        PlanWithMovies updated = subscriptionPlanCommandHandler.handle(new RemoveMoviesFromPlanCommand(id, movieIds));
        return ResponseEntity.ok(mapper.toDetailsDto(updated.plan(), updated.movies()));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePlan(@PathVariable Long id) {
        subscriptionPlanCommandHandler.handle(new DeletePlanCommand(id));
        return ResponseEntity.noContent().build();
    }
}