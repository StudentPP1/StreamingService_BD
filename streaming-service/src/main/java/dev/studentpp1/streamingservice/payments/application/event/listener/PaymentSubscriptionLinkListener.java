package dev.studentpp1.streamingservice.payments.application.event.listener;

import dev.studentpp1.streamingservice.payments.domain.model.Payment;
import dev.studentpp1.streamingservice.payments.domain.repository.PaymentRepository;
import dev.studentpp1.streamingservice.subscription.domain.event.SubscriptionLinkedToPayment;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentSubscriptionLinkListener {

    private final PaymentRepository paymentRepository;

    @EventListener
    @Transactional
    public void onSubscriptionLinked(SubscriptionLinkedToPayment event) {
        Payment payment = paymentRepository
                .findByProviderSessionIdForUpdate(event.providerSessionId())
                .orElseThrow(() -> new IllegalStateException(
                        "Payment not found for providerSessionId=" + event.providerSessionId()));
        if (payment.getUserSubscriptionId() != null) {
            if (payment.getUserSubscriptionId().equals(event.subscriptionId())) {
                log.debug("Payment already linked to subscription, paymentId={}, subscriptionId={}",
                        payment.getId(), event.subscriptionId());
                return;
            }
            throw new IllegalStateException("Payment already linked to another subscription");
        }
        payment.assignSubscription(event.subscriptionId());
        paymentRepository.save(payment);
        log.info("Linked payment to subscription: paymentId={}, subscriptionId={}",
                payment.getId(), event.subscriptionId());
    }
}

