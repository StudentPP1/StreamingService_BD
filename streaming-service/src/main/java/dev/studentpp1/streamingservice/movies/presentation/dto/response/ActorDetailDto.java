package dev.studentpp1.streamingservice.movies.presentation.dto.response;

import java.util.List;

public record ActorDetailDto(
        Long id,
        String name,
        String surname,
        String biography,
        List<ActorFilmographyDto> filmography
) {}