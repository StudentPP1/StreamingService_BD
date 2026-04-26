package dev.studentpp1.streamingservice.auth.domain.model;

import java.time.LocalDate;

public record AuthRegistrationData(
        String name,
        String surname,
        String email,
        String password,
        LocalDate birthday
) {
}

