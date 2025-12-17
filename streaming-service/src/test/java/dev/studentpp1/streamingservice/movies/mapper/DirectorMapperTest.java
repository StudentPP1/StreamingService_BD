package dev.studentpp1.streamingservice.movies.mapper;

import dev.studentpp1.streamingservice.movies.dto.DirectorDetailDto;
import dev.studentpp1.streamingservice.movies.dto.DirectorDto;
import dev.studentpp1.streamingservice.movies.dto.DirectorRequest;
import dev.studentpp1.streamingservice.movies.dto.MovieDto;
import dev.studentpp1.streamingservice.movies.entity.Director;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = DirectorMapperTest.Config.class)
class DirectorMapperTest {

    @Configuration
    @ComponentScan(basePackageClasses = DirectorMapper.class)
    static class Config {}

    @Autowired
    private DirectorMapper directorMapper;

    @Test
    void toDto_ShouldMapFieldsCorrectly() {
        Director director = new Director();
        director.setId(1L);
        director.setName("Christopher");
        director.setSurname("Nolan");
        director.setBiography("Bio");

        DirectorDto dto = directorMapper.toDto(director);

        assertThat(dto.id()).isEqualTo(1L);
        assertThat(dto.name()).isEqualTo("Christopher");
        assertThat(dto.surname()).isEqualTo("Nolan");
    }

    @Test
    void toEntity_ShouldMapRequestFields() {
        DirectorRequest request = new DirectorRequest("Quentin", "Tarantino", "Bio");

        Director director = directorMapper.toEntity(request);

        assertThat(director.getName()).isEqualTo("Quentin");
        assertThat(director.getSurname()).isEqualTo("Tarantino");
        assertThat(director.getId()).isNull();
    }

    @Test
    void updateDirectorFromRequest_ShouldUpdateOnlyNonNullFields() {
        Director director = new Director();
        director.setName("Old Name");
        director.setSurname("Old Surname");
        director.setBiography("Old Bio");
        
        DirectorRequest request = new DirectorRequest("New Name", "New Surname", null);

        directorMapper.updateDirectorFromRequest(request, director);

        assertThat(director.getName()).isEqualTo("New Name");
        assertThat(director.getSurname()).isEqualTo("New Surname");
        assertThat(director.getBiography()).isEqualTo("Old Bio");
    }

    @Test
    void toDetailDto_ShouldMapDirectorAndMovies() {
        Director director = new Director();
        director.setId(1L);
        director.setName("Guy");
        director.setSurname("Ritchie");

        MovieDto movieDto = new MovieDto(10L, "Snatch", "Desc", 2000, null, 1L, null);
        List<MovieDto> movies = List.of(movieDto);

        DirectorDetailDto detailDto = directorMapper.toDetailDto(director, movies);

        assertThat(detailDto.name()).isEqualTo("Guy");
        assertThat(detailDto.movies()).hasSize(1);
        assertThat(detailDto.movies().get(0).title()).isEqualTo("Snatch");
    }
}