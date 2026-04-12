package dev.studentpp1.streamingservice.movies.application.command.director;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record DirectorCreateRequest(
        @NotBlank(message = "Name is required")
        @Size(max = 100, message = "Name must be less than 100 characters")
        String name,

        @NotBlank(message = "Surname is required")
        @Size(max = 100, message = "Surname must be less than 100 characters")
        String surname,

        String biography
) {}
