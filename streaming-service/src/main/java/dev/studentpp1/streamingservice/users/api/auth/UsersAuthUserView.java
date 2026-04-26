package dev.studentpp1.streamingservice.users.api.auth;

public record UsersAuthUserView(
        Long id,
        String email,
        String password,
        String role
) {
}

