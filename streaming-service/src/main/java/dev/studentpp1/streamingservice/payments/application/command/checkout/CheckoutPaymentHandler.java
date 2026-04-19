package dev.studentpp1.streamingservice.payments.application.command.checkout;

import com.stripe.Stripe;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import dev.studentpp1.streamingservice.payments.domain.factory.PaymentFactory;
import dev.studentpp1.streamingservice.payments.domain.model.CheckoutPaymentRequest;
import dev.studentpp1.streamingservice.payments.domain.model.CheckoutPaymentResponse;
import dev.studentpp1.streamingservice.payments.domain.model.Payment;
import dev.studentpp1.streamingservice.payments.domain.model.PaymentStatus;
import dev.studentpp1.streamingservice.payments.domain.port.PaymentCheckoutGateway;
import dev.studentpp1.streamingservice.payments.domain.repository.PaymentRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class CheckoutPaymentHandler implements PaymentCheckoutGateway {

    public static final String SESSION_CREATED = "Payment session created";
    public static final String METADATA_USER_ID = "userId";
    public static final String METADATA_USER_EMAIL = "userEmail";
    public static final String METADATA_PRODUCT_NAME = "productName";
    public static final long QUANTITY = 1L;
    public static final BigDecimal TO_CENTS_MULTIPLIER = new BigDecimal(100);

    @Value("${app.payment.key.secret}")
    private String secretKey;
    @Value("${app.payment.url.success}")
    private String successUrl;
    @Value("${app.payment.url.cancel}")
    private String cancelUrl;
    @Value("${app.payment.currency}")
    private String currency;

    private final PaymentRepository paymentRepository;
    private final PaymentFactory paymentFactory;

    @PostConstruct
    void initStripe() {
        Stripe.apiKey = secretKey;
    }

    @Override
    @Transactional
    public CheckoutPaymentResponse checkout(CheckoutPaymentRequest command) {
        Session session = createCheckoutSession(command);
        Payment payment = paymentFactory.createNewPayment(
                session.getId(),
                command.price(),
                currency,
                command.userId(),
                command.productName()
        );
        Payment saved = paymentRepository.save(payment);
        log.info("Payment record created: id={}, sessionId={}, userId={}, product={}",
                saved.getId(), session.getId(), command.userId(), command.productName());
        return new CheckoutPaymentResponse(
                PaymentStatus.PENDING.name(),
                SESSION_CREATED,
                session.getId(),
                session.getUrl()
        );
    }

    private Session createCheckoutSession(CheckoutPaymentRequest command) {
        SessionCreateParams params = SessionCreateParams.builder()
                .setMode(SessionCreateParams.Mode.PAYMENT)
                .setSuccessUrl(successUrl)
                .setCancelUrl(cancelUrl)
                .setClientReferenceId(command.userId().toString())
                .addLineItem(buildLineItem(command.productName(), command.price()))
                .putMetadata(METADATA_USER_ID, command.userId().toString())
                .putMetadata(METADATA_USER_EMAIL, command.userEmail())
                .putMetadata(METADATA_PRODUCT_NAME, command.productName())
                .build();
        try {
            return Session.create(params);
        } catch (Exception e) {
            log.error("Failed to create Stripe session for user={}", command.userId(), e);
            throw new RuntimeException("Stripe integration error", e);
        }
    }

    private SessionCreateParams.LineItem buildLineItem(String name, BigDecimal price) {
        long amountInCents = price.multiply(TO_CENTS_MULTIPLIER).longValue();
        return SessionCreateParams.LineItem.builder()
                .setQuantity(QUANTITY)
                .setPriceData(
                        SessionCreateParams.LineItem.PriceData.builder()
                                .setCurrency(currency)
                                .setUnitAmount(amountInCents)
                                .setProductData(
                                        SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                                .setName(name)
                                                .build()
                                )
                                .build()
                )
                .build();
    }

    @Scheduled(cron = "0 30 3 * * *")
    public void deleteOldPayments() {
        LocalDateTime threshold = LocalDateTime.now().minusYears(1);
        int deleted = paymentRepository.deletePaymentsBefore(threshold);
        log.info("Cleanup: deleted {} payments older than {}", deleted, threshold);
    }
}
