package dev.studentpp1.streamingservice.movies.presentation.mapper;

import dev.studentpp1.streamingservice.movies.application.usecase.MovieService.MovieDetails;
import dev.studentpp1.streamingservice.movies.domain.model.Movie;
import dev.studentpp1.streamingservice.movies.presentation.dto.DirectorDto;
import dev.studentpp1.streamingservice.movies.presentation.dto.MovieCastDto;
import dev.studentpp1.streamingservice.movies.presentation.dto.MovieDetailDto;
import dev.studentpp1.streamingservice.movies.presentation.dto.MovieDto;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface MoviePresentationMapper {

    MovieDto toDto(Movie movie);

    List<MovieDto> toDtoList(List<Movie> movies);

    default MovieDetailDto toDetailDto(MovieDetails details) {
        return new MovieDetailDto(
                details.movie().getId(),
                details.movie().getTitle(),
                details.movie().getDescription(),
                details.movie().getYear(),
                details.movie().getRating(),
                new DirectorDto(
                        details.director().getId(),
                        details.director().getName(),
                        details.director().getSurname(),
                        details.director().getBiography()
                ),
                details.cast().stream()
                        .map(item -> new MovieCastDto(
                                item.actor().getId(),
                                item.actor().getName(),
                                item.actor().getSurname(),
                                item.performance().getCharacterName()
                        ))
                        .toList(),
                details.movie().getVersion()
        );
    }
}