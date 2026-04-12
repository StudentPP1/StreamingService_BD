package dev.studentpp1.streamingservice.users.application.command;

import dev.studentpp1.streamingservice.users.application.usecase.UserService;
import dev.studentpp1.streamingservice.users.domain.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserCommandHandler {

    private final UserService userService;

    public User handle(UpdateUserCommand command) {
        return userService.updateUser(command.request(), command.currentUserEmail());
    }

    public void handle(DeleteUserCommand command) {
        userService.softDeleteUser(command.currentUserEmail());
    }
}

