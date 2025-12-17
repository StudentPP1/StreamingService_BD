package dev.studentpp1.streamingservice.movies.repository;

import dev.studentpp1.streamingservice.AbstractPostgresContainerTest;
import dev.studentpp1.streamingservice.movies.entity.Director;
import dev.studentpp1.streamingservice.movies.entity.Movie;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.test.annotation.DirtiesContext;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
class MovieRepositoryTest extends AbstractPostgresContainerTest {

    @Autowired
    private MovieRepository movieRepository;
    @Autowired
    private DirectorRepository directorRepository;
    @Autowired
    private PerformanceRepository performanceRepository;
    @Autowired
    private ActorRepository actorRepository;
    @Autowired
    private EntityManager entityManager;

    private Director villeneuve;
    private Movie dune;
    private Movie arrival;

    @BeforeEach
    void setUp() {
        entityManager.createNativeQuery("DELETE FROM included_movie").executeUpdate();
        performanceRepository.deleteAll();
        movieRepository.deleteAll();
        directorRepository.deleteAll();
        actorRepository.deleteAll();

        villeneuve = new Director();
        villeneuve.setName("Denis");
        villeneuve.setSurname("Villeneuve");
        villeneuve = directorRepository.save(villeneuve);

        dune = new Movie();
        dune.setTitle("Dune");
        dune.setYear(2021);
        dune.setDirector(villeneuve);
        dune = movieRepository.save(dune);

        arrival = new Movie();
        arrival.setTitle("Arrival");
        arrival.setYear(2016);
        arrival.setDirector(villeneuve);
        arrival = movieRepository.save(arrival);
    }

    @Test
    void findAllByDirectorId_ShouldReturnMovies() {
        List<Movie> movies = movieRepository.findAllByDirectorId(villeneuve.getId());

        assertThat(movies).hasSize(2);
        assertThat(movies).extracting(Movie::getTitle)
                .containsExactlyInAnyOrder("Dune", "Arrival");
    }

    @Test
    void findAllByDirectorId_ShouldReturnEmpty_WhenDirectorHasNoMovies() {
        Director spielberg = new Director();
        spielberg.setName("Steven");
        spielberg.setSurname("Spielberg");
        spielberg = directorRepository.save(spielberg);

        List<Movie> movies = movieRepository.findAllByDirectorId(spielberg.getId());

        assertThat(movies).isEmpty();
    }

    @Test
    void findById_ShouldReturnMovie() {
        Optional<Movie> found = movieRepository.findById(dune.getId());

        assertThat(found).isPresent();
        assertThat(found.get().getTitle()).isEqualTo("Dune");
    }
}