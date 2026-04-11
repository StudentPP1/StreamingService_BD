package dev.studentpp1.streamingservice.subscription.domain.port;

import dev.studentpp1.streamingservice.subscription.domain.model.SubscriptionMovie;
import java.util.List;

public interface MovieProvider {
    List<SubscriptionMovie> findAllById(List<Long> ids);
}