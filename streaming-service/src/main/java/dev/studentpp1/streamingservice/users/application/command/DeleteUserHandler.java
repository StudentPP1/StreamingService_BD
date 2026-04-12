package dev.studentpp1.streamingservice.users.application.command;

import dev.studentpp1.streamingservice.users.domain.exception.UserNotFoundException;
import dev.studentpp1.streamingservice.users.domain.model.User;
import dev.studentpp1.streamingservice.users.domain.model.vo.Email;
import dev.studentpp1.streamingservice.users.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class DeleteUserHandler {

    private final UserRepository userRepository;

    @Transactional
    public void handle(DeleteUserCommand command) {
        Email email = new Email(command.currentUserEmail());
        User user = userRepository.findByEmail(email.value())
                .orElseThrow(() -> new UserNotFoundException(command.currentUserEmail()));
        user.softDelete();
        userRepository.save(user);
    }
}

