package dev.studentpp1.streamingservice.movies.presentation.dto.response;

public record DirectorDto(
        Long id,
        String name,
        String surname,
        String biography
) {}