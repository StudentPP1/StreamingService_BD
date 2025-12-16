package dev.studentpp1.streamingservice.movies.repository;

import dev.studentpp1.streamingservice.AbstractPostgresContainerTest;
import dev.studentpp1.streamingservice.movies.entity.Director;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class DirectorRepositoryTest extends AbstractPostgresContainerTest {

    @Autowired
    private DirectorRepository directorRepository;

    @Test
    void save_ShouldPersistDirector() {
        Director director = new Director();
        director.setName("Christopher");
        director.setSurname("Nolan");
        director.setBiography("British-American film director");

        Director savedDirector = directorRepository.save(director);

        assertThat(savedDirector.getId()).isNotNull();
        assertThat(savedDirector.getName()).isEqualTo("Christopher");
    }

    @Test
    void findById_ShouldReturnDirector_WhenExists() {
        Director director = new Director();
        director.setName("Quentin");
        director.setSurname("Tarantino");

        directorRepository.saveAndFlush(director);

        Optional<Director> found = directorRepository.findById(director.getId());

        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("Quentin");
    }
}