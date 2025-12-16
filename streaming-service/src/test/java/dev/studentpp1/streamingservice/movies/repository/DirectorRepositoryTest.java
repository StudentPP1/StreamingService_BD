package dev.studentpp1.streamingservice.movies.repository;

import dev.studentpp1.streamingservice.AbstractPostgresContainerTest;
import dev.studentpp1.streamingservice.movies.entity.Director;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.annotation.DirtiesContext;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class DirectorRepositoryTest extends AbstractPostgresContainerTest {

    @Autowired
    private DirectorRepository directorRepository;
    @Autowired
    private MovieRepository movieRepository;
    @Autowired
    private PerformanceRepository performanceRepository;
    @Autowired
    private ActorRepository actorRepository;
    @Autowired
    private EntityManager entityManager;

    private Director nolan;

    @BeforeEach
    void setUp() {
        performanceRepository.deleteAll();
        movieRepository.deleteAll();
        directorRepository.deleteAll();
        actorRepository.deleteAll();

        nolan = new Director();
        nolan.setName("Christopher");
        nolan.setSurname("Nolan");
        nolan.setBiography("British-American filmmaker");
        nolan = directorRepository.save(nolan);
    }

    @Test
    void findById_ShouldReturnDirector_WhenExists() {
        Optional<Director> found = directorRepository.findById(nolan.getId());

        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("Christopher");
    }

    @Test
    void findById_ShouldReturnEmpty_WhenNotFound() {
        Optional<Director> found = directorRepository.findById(999999L);
        assertThat(found).isEmpty();
    }

    @Test
    void save_ShouldPersistDirector() {
        Director tarantino = new Director();
        tarantino.setName("Quentin");
        tarantino.setSurname("Tarantino");

        Director saved = directorRepository.save(tarantino);

        assertThat(saved.getId()).isNotNull();
        assertThat(directorRepository.findById(saved.getId())).isPresent();
    }

    @Test
    void delete_ShouldRemoveDirector() {
        directorRepository.deleteById(nolan.getId());
        assertThat(directorRepository.findById(nolan.getId())).isEmpty();
    }
}