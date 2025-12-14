package dev.studentpp1.streamingservice.subscription.service;

import dev.studentpp1.streamingservice.subscription.repository.UserSubscriptionRepository;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class SubscriptionExpirationScheduler {

    private final UserSubscriptionRepository userSubscriptionRepository;

    @Transactional
    @Scheduled(cron = "${app.subscription.expire-cron}")
    public void expireSubscriptions() {
        LocalDateTime now = LocalDateTime.now();

        int updatedCount = userSubscriptionRepository.expireOverdueSubscriptions(now);

        if (updatedCount > 0) {
            log.info("Expired {} subscription(s) at {}", updatedCount, now);
        }
    }
}

