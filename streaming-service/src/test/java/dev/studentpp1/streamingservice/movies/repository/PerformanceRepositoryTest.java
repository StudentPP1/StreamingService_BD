package dev.studentpp1.streamingservice.movies.repository;

import dev.studentpp1.streamingservice.AbstractPostgresContainerTest;
import dev.studentpp1.streamingservice.movies.entity.Actor;
import dev.studentpp1.streamingservice.movies.entity.Director;
import dev.studentpp1.streamingservice.movies.entity.Movie;
import dev.studentpp1.streamingservice.movies.entity.Performance;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.test.annotation.DirtiesContext;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
class PerformanceRepositoryTest extends AbstractPostgresContainerTest {

    @Autowired
    private PerformanceRepository performanceRepository;
    @Autowired
    private MovieRepository movieRepository;
    @Autowired
    private DirectorRepository directorRepository;
    @Autowired
    private ActorRepository actorRepository;
    @Autowired
    private EntityManager entityManager;

    private Performance neoPerformance;

    @BeforeEach
    void setUp() {
        entityManager.createNativeQuery("DELETE FROM included_movie").executeUpdate();
        performanceRepository.deleteAll();
        movieRepository.deleteAll();
        directorRepository.deleteAll();
        actorRepository.deleteAll();

        Director wachowski = new Director();
        wachowski.setName("Lana");
        wachowski.setSurname("Wachowski");
        wachowski.setBiography("Director bio");
        wachowski = directorRepository.save(wachowski);

        Movie matrix = new Movie();
        matrix.setTitle("The Matrix");
        matrix.setYear(1999);
        matrix.setDescription("Sci-fi movie");
        matrix.setDirector(wachowski);
        matrix = movieRepository.save(matrix);

        Actor reeves = new Actor();
        reeves.setName("Keanu");
        reeves.setSurname("Reeves");
        reeves.setBiography("Actor bio");
        reeves = actorRepository.save(reeves);

        neoPerformance = new Performance();
        neoPerformance.setCharacterName("Neo");
        neoPerformance.setDescription("The One");
        neoPerformance.setMovie(matrix);
        neoPerformance.setActor(reeves);
        neoPerformance = performanceRepository.save(neoPerformance);
    }

    @Test
    void findById_ShouldReturnPerformanceWithAssociations() {
        Optional<Performance> found = performanceRepository.findById(neoPerformance.getId());

        assertThat(found).isPresent();
        assertThat(found.get().getCharacterName()).isEqualTo("Neo");
        assertThat(found.get().getMovie().getTitle()).isEqualTo("The Matrix");
        assertThat(found.get().getActor().getSurname()).isEqualTo("Reeves");
    }

    @Test
    void delete_ShouldRemovePerformance() {
        performanceRepository.deleteById(neoPerformance.getId());

        assertThat(performanceRepository.findById(neoPerformance.getId())).isEmpty();

        assertThat(movieRepository.findAll()).isNotEmpty();
        assertThat(actorRepository.findAll()).isNotEmpty();
    }
}