package dev.studentpp1.streamingservice.movies.mapper;

import dev.studentpp1.streamingservice.movies.dto.PerformanceDto;
import dev.studentpp1.streamingservice.movies.dto.PerformanceRequest;
import dev.studentpp1.streamingservice.movies.entity.Actor;
import dev.studentpp1.streamingservice.movies.entity.Movie;
import dev.studentpp1.streamingservice.movies.entity.Performance;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = PerformanceMapperTest.Config.class)
class PerformanceMapperTest {

    @Configuration
    @ComponentScan(basePackageClasses = PerformanceMapper.class)
    static class Config {}

    @Autowired
    private PerformanceMapper performanceMapper;

    @Test
    void toDto_ShouldMapIds() {
        Actor actor = new Actor();
        actor.setId(10L);

        Movie movie = new Movie();
        movie.setId(20L);

        Performance performance = new Performance();
        performance.setId(1L);
        performance.setCharacterName("Joker");
        performance.setActor(actor);
        performance.setMovie(movie);

        PerformanceDto dto = performanceMapper.toDto(performance);

        assertThat(dto.actorId()).isEqualTo(10L);
        assertThat(dto.movieId()).isEqualTo(20L);
        assertThat(dto.characterName()).isEqualTo("Joker");
    }

    @Test
    void toEntity_ShouldIgnoreRelations() {
        PerformanceRequest request = new PerformanceRequest("Joker", "Desc", 10L, 20L);

        Performance performance = performanceMapper.toEntity(request);

        assertThat(performance.getCharacterName()).isEqualTo("Joker");
        assertThat(performance.getActor()).isNull();
        assertThat(performance.getMovie()).isNull();
    }
}