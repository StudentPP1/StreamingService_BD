package dev.studentpp1.streamingservice.movies.presentation.mapper;

import dev.studentpp1.streamingservice.movies.domain.model.Performance;
import dev.studentpp1.streamingservice.movies.presentation.dto.PerformanceDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PerformancePresentationMapper {

    PerformanceDto toDto(Performance performance);
}