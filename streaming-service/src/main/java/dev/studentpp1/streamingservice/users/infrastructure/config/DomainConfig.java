package dev.studentpp1.streamingservice.users.infrastructure.config;

import dev.studentpp1.streamingservice.users.domain.factory.UserFactory;
import dev.studentpp1.streamingservice.users.domain.port.PasswordHasher;
import dev.studentpp1.streamingservice.users.domain.repository.UserRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration("usersDomainConfig")
public class DomainConfig {

    @Bean
    public UserFactory userFactory(UserRepository userRepository, PasswordHasher passwordHasher) {
        return new UserFactory(userRepository, passwordHasher);
    }
}
