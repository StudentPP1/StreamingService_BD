package dev.studentpp1.streamingservice.subscription.infrastructure.adapter;

import dev.studentpp1.streamingservice.subscription.domain.model.SubscriberContext;
import dev.studentpp1.streamingservice.subscription.domain.port.SubscriberProvider;
import dev.studentpp1.streamingservice.users.domain.exception.UserNotFoundException;
import dev.studentpp1.streamingservice.users.domain.model.User;
import dev.studentpp1.streamingservice.users.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserServiceAdapter implements SubscriberProvider {

    private final UserRepository userRepository;

    @Override
    public SubscriberContext getById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));
        return mapToContext(user);
    }

    @Override
    public SubscriberContext getByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException(email));
        return mapToContext(user);
    }

    private SubscriberContext mapToContext(User user) {
        return new SubscriberContext(user.getId(), user.getEmail());
    }
}