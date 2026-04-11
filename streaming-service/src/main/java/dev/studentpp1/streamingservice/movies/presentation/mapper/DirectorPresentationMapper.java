package dev.studentpp1.streamingservice.movies.presentation.mapper;

import dev.studentpp1.streamingservice.movies.domain.model.Director;
import dev.studentpp1.streamingservice.movies.presentation.dto.response.DirectorDetailDto;
import dev.studentpp1.streamingservice.movies.presentation.dto.response.DirectorDto;
import dev.studentpp1.streamingservice.movies.presentation.dto.response.MovieDto;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface DirectorPresentationMapper {

    DirectorDto toDto(Director director);

    default DirectorDetailDto toDetailDto(Director director, List<MovieDto> movies) {
        return new DirectorDetailDto(
                director.getId(),
                director.getName(),
                director.getSurname(),
                director.getBiography(),
                movies
        );
    }
}