package dev.studentpp1.streamingservice.movies.mapper;

import dev.studentpp1.streamingservice.movies.dto.MovieDetailDto;
import dev.studentpp1.streamingservice.movies.dto.MovieDto;
import dev.studentpp1.streamingservice.movies.dto.MovieRequest;
import dev.studentpp1.streamingservice.movies.entity.Actor;
import dev.studentpp1.streamingservice.movies.entity.Director;
import dev.studentpp1.streamingservice.movies.entity.Movie;
import dev.studentpp1.streamingservice.movies.entity.Performance;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = MovieMapperTest.Config.class)
class MovieMapperTest {

    @Configuration
    @ComponentScan(basePackageClasses = MovieMapper.class)
    static class Config {}

    @Autowired
    private MovieMapper movieMapper;

    @Test
    void toDto_ShouldMapDirectorId() {
        Director director = new Director();
        director.setId(55L);

        Movie movie = new Movie();
        movie.setId(1L);
        movie.setTitle("Dune");
        movie.setDirector(director);

        MovieDto dto = movieMapper.toDto(movie);

        assertThat(dto.title()).isEqualTo("Dune");
        assertThat(dto.directorId()).isEqualTo(55L);
    }

    @Test
    void toEntity_ShouldIgnoreDirectorAndId() {
        MovieRequest request = new MovieRequest("Dune 2", "Desc", 2024, BigDecimal.TEN, 55L);

        Movie movie = movieMapper.toEntity(request);

        assertThat(movie.getTitle()).isEqualTo("Dune 2");
        assertThat(movie.getDirector()).isNull();
        assertThat(movie.getId()).isNull();
    }

    @Test
    void toDetailDto_ShouldUseDirectorMapperAndMapCast() {
        Director director = new Director();
        director.setId(55L);
        director.setName("Denis");
        director.setSurname("Villeneuve");

        Actor actor = new Actor();
        actor.setId(10L);
        actor.setName("Timothee");
        actor.setSurname("Chalamet");

        Movie movie = new Movie();
        movie.setTitle("Dune");
        movie.setDirector(director);

        Performance performance = new Performance();
        performance.setActor(actor);
        performance.setCharacterName("Paul Atreides");

        movie.setPerformances(List.of(performance));

        MovieDetailDto detailDto = movieMapper.toDetailDto(movie);

        assertThat(detailDto.director().name()).isEqualTo("Denis");

        assertThat(detailDto.cast()).hasSize(1);
        assertThat(detailDto.cast().get(0).actorName()).isEqualTo("Timothee");
    }
}