package dev.studentpp1.streamingservice.subscription.domain.port;

import dev.studentpp1.streamingservice.subscription.domain.model.SubscriberContext;

public interface SubscriberProvider {
    SubscriberContext getById(Long id);
    SubscriberContext getByEmail(String email);
}
