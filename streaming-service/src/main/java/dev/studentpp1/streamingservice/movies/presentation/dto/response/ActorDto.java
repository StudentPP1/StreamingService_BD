package dev.studentpp1.streamingservice.movies.presentation.dto.response;

public record ActorDto(
        Long id,
        String name,
        String surname,
        String biography
) {}