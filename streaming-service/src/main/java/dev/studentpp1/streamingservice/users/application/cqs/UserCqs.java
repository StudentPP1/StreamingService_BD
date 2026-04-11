package dev.studentpp1.streamingservice.users.application.cqs;

import dev.studentpp1.streamingservice.auth.dto.RegisterUserRequest;
import dev.studentpp1.streamingservice.users.presentation.dto.UpdateUserRequest;

public final class UserCqs {
    private UserCqs() {
    }

    public record GetCurrentUserQuery(String currentUserEmail) {
    }

    public record CreateUserCommand(RegisterUserRequest request) {
    }

    public record UpdateUserCommand(UpdateUserRequest request, String currentUserEmail) {
    }

    public record DeleteUserCommand(String currentUserEmail) {
    }
}
