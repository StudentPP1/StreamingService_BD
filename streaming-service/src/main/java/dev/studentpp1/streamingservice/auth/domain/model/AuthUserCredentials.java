package dev.studentpp1.streamingservice.auth.domain.model;

public record AuthUserCredentials(
        Long id,
        String email,
        String password,
        String role
) {
}

