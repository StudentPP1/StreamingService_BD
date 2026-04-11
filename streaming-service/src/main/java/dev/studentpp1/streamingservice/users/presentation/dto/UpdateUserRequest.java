package dev.studentpp1.streamingservice.users.presentation.dto;

import java.time.LocalDate;

public record UpdateUserRequest(
        String name,
        String surname,
        LocalDate birthday
) {
}
