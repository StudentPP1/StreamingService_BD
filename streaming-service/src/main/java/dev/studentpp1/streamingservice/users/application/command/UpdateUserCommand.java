package dev.studentpp1.streamingservice.users.application.command;

import dev.studentpp1.streamingservice.users.application.dto.UpdateUserRequest;

public record UpdateUserCommand(UpdateUserRequest request, String currentUserEmail) {
}

