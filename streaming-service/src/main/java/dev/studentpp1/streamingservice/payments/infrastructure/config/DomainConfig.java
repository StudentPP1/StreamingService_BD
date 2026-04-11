package dev.studentpp1.streamingservice.payments.infrastructure.config;

import dev.studentpp1.streamingservice.payments.domain.factory.PaymentFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration("paymentsDomainConfig")
public class DomainConfig {

    @Bean
    public PaymentFactory paymentFactory() {
        return new PaymentFactory();
    }
}

