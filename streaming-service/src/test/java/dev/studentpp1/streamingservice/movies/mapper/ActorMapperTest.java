package dev.studentpp1.streamingservice.movies.mapper;

import dev.studentpp1.streamingservice.movies.dto.ActorDetailDto;
import dev.studentpp1.streamingservice.movies.dto.ActorDto;
import dev.studentpp1.streamingservice.movies.dto.ActorRequest;
import dev.studentpp1.streamingservice.movies.entity.Actor;
import dev.studentpp1.streamingservice.movies.entity.Movie;
import dev.studentpp1.streamingservice.movies.entity.Performance;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = ActorMapperTest.Config.class)
class ActorMapperTest {

    @Configuration
    @ComponentScan(basePackageClasses = ActorMapper.class)
    static class Config {}

    @Autowired
    private ActorMapper actorMapper;

    @Test
    void toDto_ShouldMapBasicFields() {
        Actor actor = new Actor();
        actor.setId(1L);
        actor.setName("Brad");
        actor.setSurname("Pitt");

        ActorDto dto = actorMapper.toDto(actor);

        assertThat(dto.name()).isEqualTo("Brad");
        assertThat(dto.surname()).isEqualTo("Pitt");
    }

    @Test
    void toEntity_ShouldMapRequest() {
        ActorRequest request = new ActorRequest("Brad", "Pitt", "Bio");

        Actor actor = actorMapper.toEntity(request);

        assertThat(actor.getName()).isEqualTo("Brad");
    }

    @Test
    void toDetailDto_ShouldMapFilmography() {
        Actor actor = new Actor();
        actor.setId(1L);
        actor.setName("Keanu");

        Movie movie = new Movie();
        movie.setId(100L);
        movie.setTitle("Matrix");
        movie.setYear(1999);

        Performance performance = new Performance();
        performance.setCharacterName("Neo");
        performance.setMovie(movie);
        performance.setActor(actor);

        actor.setPerformances(List.of(performance));

        ActorDetailDto detailDto = actorMapper.toDetailDto(actor);

        assertThat(detailDto.name()).isEqualTo("Keanu");
        assertThat(detailDto.filmography()).hasSize(1);

        var filmItem = detailDto.filmography().get(0);
        assertThat(filmItem.movieTitle()).isEqualTo("Matrix");
        assertThat(filmItem.characterName()).isEqualTo("Neo");
        assertThat(filmItem.movieYear()).isEqualTo(1999);
    }
}