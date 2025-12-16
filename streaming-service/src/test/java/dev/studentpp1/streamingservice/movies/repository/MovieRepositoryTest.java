package dev.studentpp1.streamingservice.movies.repository;

import dev.studentpp1.streamingservice.AbstractPostgresContainerTest;
import dev.studentpp1.streamingservice.movies.entity.Director;
import dev.studentpp1.streamingservice.movies.entity.Movie;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class MovieRepositoryTest extends AbstractPostgresContainerTest {

    @Autowired
    private MovieRepository movieRepository;

    @Autowired
    private DirectorRepository directorRepository;

    @Test
    void findAllByDirectorId_ShouldReturnMovies_WhenDirectorExists() {
        Director director = new Director();
        director.setName("Denis");
        director.setSurname("Villeneuve");
        directorRepository.saveAndFlush(director);

        Movie movie1 = new Movie();
        movie1.setTitle("Dune");
        movie1.setYear(2021);
        movie1.setDirector(director);
        movieRepository.save(movie1);

        Movie movie2 = new Movie();
        movie2.setTitle("Arrival");
        movie2.setYear(2016);
        movie2.setDirector(director);
        movieRepository.save(movie2);

        Director otherDirector = new Director();
        otherDirector.setName("Steven");
        otherDirector.setSurname("Spielberg");
        directorRepository.saveAndFlush(otherDirector);

        Movie otherMovie = new Movie();
        otherMovie.setTitle("Jaws");
        otherMovie.setYear(1975);
        otherMovie.setDirector(otherDirector);
        movieRepository.save(otherMovie);

        movieRepository.flush();

        List<Movie> result = movieRepository.findAllByDirectorId(director.getId());

        assertThat(result).hasSize(2);
        assertThat(result).extracting(Movie::getTitle)
                .containsExactlyInAnyOrder("Dune", "Arrival");

        assertThat(result).extracting(Movie::getTitle)
                .doesNotContain("Jaws");
    }

    @Test
    void save_ShouldFail_WhenDirectorIsNull() {
        Movie movie = new Movie();
        movie.setTitle("No Director Movie");
        movie.setYear(2022);

        try {
            movieRepository.saveAndFlush(movie);
        } catch (Exception e) {
            assertThat(e).isInstanceOf(org.springframework.dao.DataIntegrityViolationException.class);
        }
    }
}