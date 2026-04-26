package dev.studentpp1.streamingservice.auth.presentation.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record LoginUserRequest(
        @NotBlank
        @NotNull
        @Email
        String email,

        @NotBlank
        @NotNull
        String password

) {
}
