package dev.studentpp1.streamingservice.movies.presentation.dto.response;

public record MovieCastDto(
        Long actorId,
        String actorName,
        String actorSurname,
        String characterName
) {}