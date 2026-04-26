package dev.studentpp1.streamingservice.users.api.auth;

import java.time.LocalDate;

public record UsersCreateUserRequest(
        String name,
        String surname,
        String email,
        String password,
        LocalDate birthday
) {
}

