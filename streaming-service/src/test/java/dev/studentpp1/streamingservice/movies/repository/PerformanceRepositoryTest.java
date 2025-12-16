package dev.studentpp1.streamingservice.movies.repository;

import dev.studentpp1.streamingservice.AbstractPostgresContainerTest;
import dev.studentpp1.streamingservice.movies.entity.Actor;
import dev.studentpp1.streamingservice.movies.entity.Director;
import dev.studentpp1.streamingservice.movies.entity.Movie;
import dev.studentpp1.streamingservice.movies.entity.Performance;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class PerformanceRepositoryTest extends AbstractPostgresContainerTest {

    @Autowired
    private PerformanceRepository performanceRepository;

    @Autowired
    private ActorRepository actorRepository;

    @Autowired
    private MovieRepository movieRepository;

    @Autowired
    private DirectorRepository directorRepository;

    @Test
    void shouldSaveAndRetrievePerformanceWithAssociations() {
        Director director = new Director();
        director.setName("Lana");
        director.setSurname("Wachowski");
        directorRepository.saveAndFlush(director);

        Movie movie = new Movie();
        movie.setTitle("The Matrix");
        movie.setYear(1999);
        movie.setDirector(director);
        movieRepository.saveAndFlush(movie);

        Actor actor = new Actor();
        actor.setName("Keanu");
        actor.setSurname("Reeves");
        actorRepository.saveAndFlush(actor);

        Performance performance = new Performance();
        performance.setCharacterName("Neo");
        performance.setMovie(movie);
        performance.setActor(actor);

        Performance savedPerformance = performanceRepository.saveAndFlush(performance);

        Performance retrieved = performanceRepository.findById(savedPerformance.getId()).orElseThrow();

        assertThat(retrieved.getCharacterName()).isEqualTo("Neo");
        assertThat(retrieved.getMovie().getTitle()).isEqualTo("The Matrix");
        assertThat(retrieved.getActor().getSurname()).isEqualTo("Reeves");
    }
}