package dev.studentpp1.streamingservice.users.application.command;

import dev.studentpp1.streamingservice.users.domain.factory.UserFactory;
import dev.studentpp1.streamingservice.users.domain.model.User;
import dev.studentpp1.streamingservice.users.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class CreateUserHandler {

    private final UserRepository userRepository;
    private final UserFactory userFactory;

    @Transactional
    public void handle(CreateUserCommand command) {
        User user = userFactory.create(
                command.name(),
                command.surname(),
                command.email(),
                command.password(),
                command.birthday()
        );
        userRepository.save(user);
    }
}


