package dev.studentpp1.streamingservice.subscription.internal.acl;

import dev.studentpp1.streamingservice.payments.api.checkout.PaymentCheckoutRequest;
import dev.studentpp1.streamingservice.payments.api.checkout.PaymentCheckoutResponse;
import dev.studentpp1.streamingservice.subscription.domain.model.CheckoutCommand;
import dev.studentpp1.streamingservice.subscription.domain.model.CheckoutResult;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class SubscriptionPaymentsAclTranslatorTest {

    private final SubscriptionPaymentsAclTranslator translator = new SubscriptionPaymentsAclTranslator();

    @Test
    void mapsInternalCheckoutCommandToPaymentsApiRequest() {
        CheckoutCommand command = new CheckoutCommand("Premium", BigDecimal.TEN, 7L, "user@test.com");

        PaymentCheckoutRequest request = translator.toExternalRequest(command);

        assertThat(request.productName()).isEqualTo("Premium");
        assertThat(request.price()).isEqualTo(BigDecimal.TEN);
        assertThat(request.userId()).isEqualTo(7L);
        assertThat(request.userEmail()).isEqualTo("user@test.com");
    }

    @Test
    void mapsPaymentsApiResponseToInternalCheckoutResult() {
        PaymentCheckoutResponse response = new PaymentCheckoutResponse("PENDING", "ok", "sess_1", "url_1");

        CheckoutResult result = translator.toInternalResult(response);

        assertThat(result.status()).isEqualTo("PENDING");
        assertThat(result.message()).isEqualTo("ok");
        assertThat(result.sessionId()).isEqualTo("sess_1");
        assertThat(result.url()).isEqualTo("url_1");
    }
}


