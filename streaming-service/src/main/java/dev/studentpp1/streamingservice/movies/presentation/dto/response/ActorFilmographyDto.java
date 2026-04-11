package dev.studentpp1.streamingservice.movies.presentation.dto.response;

public record ActorFilmographyDto(
        Long movieId,
        String movieTitle,
        Integer movieYear,
        String characterName
) {}