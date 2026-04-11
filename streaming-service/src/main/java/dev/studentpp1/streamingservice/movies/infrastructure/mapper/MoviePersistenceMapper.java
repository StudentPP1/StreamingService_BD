package dev.studentpp1.streamingservice.movies.infrastructure.mapper;

import dev.studentpp1.streamingservice.movies.domain.model.Movie;
import dev.studentpp1.streamingservice.movies.infrastructure.entity.MovieEntity;
import org.springframework.stereotype.Component;

@Component
public class MoviePersistenceMapper {

    public Movie toDomain(MovieEntity entity) {
        return Movie.restore(
                entity.getId(),
                entity.getTitle(),
                entity.getDescription(),
                entity.getYear(),
                entity.getRating(),
                entity.getDirectorEntity() != null ? entity.getDirectorEntity().getId() : null,
                entity.getVersion()
        );
    }

    public MovieEntity toEntity(Movie domain) {
        MovieEntity entity = new MovieEntity();
        entity.setId(domain.getId());
        entity.setTitle(domain.getTitle());
        entity.setDescription(domain.getDescription());
        entity.setYear(domain.getYear());
        entity.setRating(domain.getRating());
        entity.setVersion(domain.getVersion());
        return entity;
    }
}