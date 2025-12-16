package dev.studentpp1.streamingservice.movies.repository;

import dev.studentpp1.streamingservice.AbstractPostgresContainerTest;
import dev.studentpp1.streamingservice.movies.entity.Actor;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class ActorRepositoryTest extends AbstractPostgresContainerTest {

    @Autowired
    private ActorRepository actorRepository;
    @Autowired
    private PerformanceRepository performanceRepository;
    @Autowired
    private MovieRepository movieRepository;
    @Autowired
    private DirectorRepository directorRepository;
    @Autowired
    private EntityManager entityManager;

    private Actor leo;

    @BeforeEach
    void setUp() {
        //entityManager.createNativeQuery("DELETE FROM included_movie").executeUpdate();
        performanceRepository.deleteAll();
        movieRepository.deleteAll();
        directorRepository.deleteAll();
        actorRepository.deleteAll();

        leo = new Actor();
        leo.setName("Leonardo");
        leo.setSurname("DiCaprio");
        leo.setBiography("Oscar winner");
        leo = actorRepository.save(leo);
    }

    @Test
    void findById_ShouldReturnActor_WhenExists() {
        Optional<Actor> found = actorRepository.findById(leo.getId());
        assertThat(found).isPresent();
        assertThat(found.get().getSurname()).isEqualTo("DiCaprio");
    }

    @Test
    void findById_ShouldReturnEmpty_WhenNotFound() {
        Optional<Actor> found = actorRepository.findById(99999L);
        assertThat(found).isEmpty();
    }

    @Test
    void save_ShouldPersistNewActor() {
        Actor pitt = new Actor();
        pitt.setName("Brad");
        pitt.setSurname("Pitt");

        Actor saved = actorRepository.save(pitt);
        assertThat(saved.getId()).isNotNull();
    }

    @Test
    void delete_ShouldRemoveActor() {
        actorRepository.deleteById(leo.getId());
        assertThat(actorRepository.findById(leo.getId())).isEmpty();
    }
}