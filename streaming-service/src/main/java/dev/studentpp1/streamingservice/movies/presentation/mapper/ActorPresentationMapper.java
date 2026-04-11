package dev.studentpp1.streamingservice.movies.presentation.mapper;

import dev.studentpp1.streamingservice.movies.application.usecase.ActorService.ActorDetails;
import dev.studentpp1.streamingservice.movies.domain.model.Actor;
import dev.studentpp1.streamingservice.movies.presentation.dto.response.ActorDetailDto;
import dev.studentpp1.streamingservice.movies.presentation.dto.response.ActorDto;
import dev.studentpp1.streamingservice.movies.presentation.dto.response.ActorFilmographyDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ActorPresentationMapper {

    ActorDto toDto(Actor actor);

    default ActorDetailDto toDetailDto(ActorDetails details) {
        return new ActorDetailDto(
                details.actor().getId(),
                details.actor().getName(),
                details.actor().getSurname(),
                details.actor().getBiography(),
                details.filmography().stream()
                        .map(item -> new ActorFilmographyDto(
                                item.movie().getId(),
                                item.movie().getTitle(),
                                item.movie().getYear(),
                                item.performance().getCharacterName()
                        ))
                        .toList()
        );
    }
}