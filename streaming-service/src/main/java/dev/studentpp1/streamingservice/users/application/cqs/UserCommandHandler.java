package dev.studentpp1.streamingservice.users.application.cqs;

import dev.studentpp1.streamingservice.users.application.cqs.UserCqs.CreateUserCommand;
import dev.studentpp1.streamingservice.users.application.cqs.UserCqs.DeleteUserCommand;
import dev.studentpp1.streamingservice.users.application.cqs.UserCqs.UpdateUserCommand;
import dev.studentpp1.streamingservice.users.application.usecase.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserCommandHandler {
    private final UserService userService;

    public Long handle(CreateUserCommand command) {
        return userService.createUser(command.request()).getId();
    }

    public Long handle(UpdateUserCommand command) {
        return userService.updateUser(command.request(), command.currentUserEmail()).getId();
    }

    public void handle(DeleteUserCommand command) {
        userService.softDeleteUser(command.currentUserEmail());
    }
}
