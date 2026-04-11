package dev.studentpp1.streamingservice.payments.application.cqs;
import com.stripe.model.Event;
public final class PaymentsCqs {
    private PaymentsCqs() {}
    public record GetUserPaymentsQuery(Long userId) {}
    public record GetPaymentsBySubscriptionQuery(Long userId, Long subscriptionId) {}
    public record HandleStripeEventCommand(Event event) {}
}
