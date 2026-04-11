package dev.studentpp1.streamingservice.payments.domain.port;

import java.util.List;

public interface PaymentCompletionHandler {
    Long handleSuccess(Long userId, String planName, List<String> emails);
}
