package dev.studentpp1.streamingservice.auth.dto;

import jakarta.validation.constraints.*;

import java.time.LocalDate;

public record RegisterUserRequest(
        @NotBlank
        @NotNull
        String name,
        @NotBlank
        @NotNull
        String surname,
        @NotBlank
        @NotNull
        @Email
        String email,
        @NotBlank
        @NotNull
        String password,
        @Past(message = "Birthday must be in the past")
        LocalDate birthday
) {
}
