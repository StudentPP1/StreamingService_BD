package dev.studentpp1.streamingservice.users.application.command;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserCommandHandler {

    private final CreateUserHandler createUserHandler;
    private final UpdateUserHandler updateUserHandler;
    private final DeleteUserHandler deleteUserHandler;

    public void handle(CreateUserCommand command) {
        createUserHandler.handle(command);
    }

    public void handle(UpdateUserCommand command) {
        updateUserHandler.handle(command);
    }

    public void handle(DeleteUserCommand command) {
        deleteUserHandler.handle(command);
    }
}

