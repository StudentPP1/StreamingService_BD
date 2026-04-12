package dev.studentpp1.streamingservice.movies.application.query.actor;

import java.util.List;

public record ActorDetailsReadModel(
        Long id,
        String name,
        String surname,
        String biography,
        List<ActorFilmographyItemReadModel> filmography
) {
}
