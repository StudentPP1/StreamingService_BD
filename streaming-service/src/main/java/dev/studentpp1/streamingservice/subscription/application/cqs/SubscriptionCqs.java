package dev.studentpp1.streamingservice.subscription.application.cqs;
import dev.studentpp1.streamingservice.subscription.presentation.dto.request.CreateFamilySubscriptionRequest;
import dev.studentpp1.streamingservice.subscription.presentation.dto.request.CreateSubscriptionPlanRequest;
import dev.studentpp1.streamingservice.subscription.presentation.dto.request.SubscribeRequest;
import java.util.List;
public final class SubscriptionCqs {
    private SubscriptionCqs() {}
    public record GetAllPlansQuery(String search, int page, int size) {}
    public record GetPlanByIdQuery(Long id) {}
    public record CreatePlanCommand(CreateSubscriptionPlanRequest request) {}
    public record UpdatePlanCommand(Long id, CreateSubscriptionPlanRequest request) {}
    public record AddMoviesToPlanCommand(Long id, List<Long> movieIds) {}
    public record RemoveMoviesFromPlanCommand(Long id, List<Long> movieIds) {}
    public record DeletePlanCommand(Long id) {}
    public record SubscribeCommand(SubscribeRequest request, Long userId) {}
    public record CreateFamilySubscriptionCommand(CreateFamilySubscriptionRequest request, Long userId) {}
    public record GetMySubscriptionsQuery(Long userId, int page, int size) {}
    public record CancelSubscriptionCommand(Long subscriptionId, Long userId) {}
}
