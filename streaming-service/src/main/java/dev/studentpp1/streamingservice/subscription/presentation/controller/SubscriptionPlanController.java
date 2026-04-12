package dev.studentpp1.streamingservice.subscription.presentation.controller;

import dev.studentpp1.streamingservice.common.dto.PageResult;
import dev.studentpp1.streamingservice.subscription.application.command.plan.AddMoviesToPlanCommand;
import dev.studentpp1.streamingservice.subscription.application.command.plan.CreatePlanCommand;
import dev.studentpp1.streamingservice.subscription.application.command.plan.DeletePlanCommand;
import dev.studentpp1.streamingservice.subscription.application.command.plan.RemoveMoviesFromPlanCommand;
import dev.studentpp1.streamingservice.subscription.application.command.SubscriptionPlanCommandHandler;
import dev.studentpp1.streamingservice.subscription.application.command.plan.UpdatePlanCommand;
import dev.studentpp1.streamingservice.subscription.application.query.GetAllPlansQuery;
import dev.studentpp1.streamingservice.subscription.application.query.GetPlanByIdQuery;
import dev.studentpp1.streamingservice.subscription.application.query.readmodel.SubscriptionPlanDetailsReadModel;
import dev.studentpp1.streamingservice.subscription.application.query.SubscriptionPlanQueryHandler;
import dev.studentpp1.streamingservice.subscription.application.query.readmodel.SubscriptionPlanSummaryReadModel;
import dev.studentpp1.streamingservice.subscription.presentation.dto.CreateSubscriptionPlanRequest;
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

    @GetMapping
    public ResponseEntity<PageResult<SubscriptionPlanSummaryReadModel>> getAllPlans(
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(subscriptionPlanQueryHandler.handle(new GetAllPlansQuery(search, page, size)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<SubscriptionPlanDetailsReadModel> getPlan(@PathVariable Long id) {
        return ResponseEntity.ok(subscriptionPlanQueryHandler.handle(new GetPlanByIdQuery(id)));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<Void> createPlan(
            @Valid @RequestBody CreateSubscriptionPlanRequest request) {
        subscriptionPlanCommandHandler.handle(new CreatePlanCommand(
                request.name(),
                request.description(),
                request.price(),
                request.duration(),
                request.includedMovieIds()
        ));
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<Void> updatePlan(
            @PathVariable Long id,
            @Valid @RequestBody CreateSubscriptionPlanRequest request) {
        subscriptionPlanCommandHandler.handle(new UpdatePlanCommand(
                id,
                request.name(),
                request.description(),
                request.price(),
                request.duration(),
                request.includedMovieIds()
        ));
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{id}/movies/add")
    public ResponseEntity<Void> addMovies(
            @PathVariable Long id,
            @RequestBody List<Long> movieIds) {
        subscriptionPlanCommandHandler.handle(new AddMoviesToPlanCommand(id, movieIds));
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{id}/movies/remove")
    public ResponseEntity<Void> removeMovies(
            @PathVariable Long id,
            @RequestBody List<Long> movieIds) {
        subscriptionPlanCommandHandler.handle(new RemoveMoviesFromPlanCommand(id, movieIds));
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePlan(@PathVariable Long id) {
        subscriptionPlanCommandHandler.handle(new DeletePlanCommand(id));
        return ResponseEntity.noContent().build();
    }
}