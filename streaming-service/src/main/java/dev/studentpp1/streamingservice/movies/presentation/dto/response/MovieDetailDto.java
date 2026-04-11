package dev.studentpp1.streamingservice.movies.presentation.dto.response;

import java.math.BigDecimal;
import java.util.List;

public record MovieDetailDto(
        Long id,
        String title,
        String description,
        Integer year,
        BigDecimal rating,
        DirectorDto director,
        List<MovieCastDto> cast,
        Long version
) {}