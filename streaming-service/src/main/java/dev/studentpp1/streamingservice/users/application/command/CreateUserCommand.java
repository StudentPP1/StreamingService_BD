package dev.studentpp1.streamingservice.users.application.command;

import java.time.LocalDate;

public record CreateUserCommand(
        String name,
        String surname,
        String email,
        String password,
        LocalDate birthday
) {
}

