package dev.studentpp1.streamingservice.analytics.service;

import dev.studentpp1.streamingservice.AbstractPostgresContainerTest;
import dev.studentpp1.streamingservice.analytics.dto.ActorAnalyticsStats;
import dev.studentpp1.streamingservice.movies.entity.Actor;
import dev.studentpp1.streamingservice.movies.entity.Director;
import dev.studentpp1.streamingservice.movies.entity.Movie;
import dev.studentpp1.streamingservice.movies.entity.Performance;
import dev.studentpp1.streamingservice.movies.repository.ActorRepository;
import dev.studentpp1.streamingservice.movies.repository.DirectorRepository;
import dev.studentpp1.streamingservice.movies.repository.MovieRepository;
import dev.studentpp1.streamingservice.movies.repository.PerformanceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@TestPropertySource(properties = "spring.jpa.hibernate.ddl-auto=create-drop")
class AnalyticsServiceIntegrationTest extends AbstractPostgresContainerTest {

    @Autowired
    private AnalyticsService analyticsService;

    @Autowired
    private ActorRepository actorRepository;

    @Autowired
    private DirectorRepository directorRepository;

    @Autowired
    private MovieRepository movieRepository;

    @Autowired
    private PerformanceRepository performanceRepository;

    @BeforeEach
    void setUp() {
        performanceRepository.deleteAll();
        movieRepository.deleteAll();
        directorRepository.deleteAll();
        actorRepository.deleteAll();
    }

    @Test
    void getActorAnalytics_ShouldCalculateRankAndStatsCorrectly() {
        Director nolan = new Director();
        nolan.setName("Christopher");
        nolan.setSurname("Nolan");
        nolan.setBiography("Bio");
        nolan = directorRepository.save(nolan);

        Movie inception = new Movie();
        inception.setTitle("Inception");
        inception.setYear(2010);
        inception.setRating(new BigDecimal("9.0"));
        inception.setDirector(nolan);
        inception.setVersion(0L);
        inception = movieRepository.save(inception);

        Movie tenet = new Movie();
        tenet.setTitle("Tenet");
        tenet.setYear(2020);
        tenet.setRating(new BigDecimal("7.0"));
        tenet.setDirector(nolan);
        tenet.setVersion(0L);
        tenet = movieRepository.save(tenet);

        Actor leo = new Actor();
        leo.setName("Leonardo");
        leo.setSurname("DiCaprio");
        leo.setBiography("Oscar winner");
        leo = actorRepository.save(leo);

        Actor pattinson = new Actor();
        pattinson.setName("Robert");
        pattinson.setSurname("Pattinson");
        pattinson.setBiography("Batman");
        pattinson = actorRepository.save(pattinson);

        Performance p1 = new Performance();
        p1.setCharacterName("Cobb");
        p1.setDescription("Main char");
        p1.setActor(leo);
        p1.setMovie(inception);
        performanceRepository.save(p1);

        Performance p2 = new Performance();
        p2.setCharacterName("Neil");
        p2.setDescription("Support char");
        p2.setActor(pattinson);
        p2.setMovie(inception);
        performanceRepository.save(p2);

        Performance p3 = new Performance();
        p3.setCharacterName("Neil");
        p3.setDescription("Main char");
        p3.setActor(pattinson);
        p3.setMovie(tenet);
        performanceRepository.save(p3);

        List<ActorAnalyticsStats> stats = analyticsService.getActorAnalytics();

        assertThat(stats).hasSize(2);

        ActorAnalyticsStats topActor = stats.get(0);
        assertThat(topActor.getFullName()).isEqualTo("Leonardo DiCaprio");
        assertThat(topActor.getTotalMovies()).isEqualTo(1);
        assertThat(topActor.getActorRating()).isEqualByComparingTo("9.0");
        assertThat(topActor.getRankInSystem()).isEqualTo(1);
        assertThat(topActor.getPerformanceStatus()).isEqualTo("Above Average");

        ActorAnalyticsStats secondActor = stats.get(1);
        assertThat(secondActor.getFullName()).isEqualTo("Robert Pattinson");
        assertThat(secondActor.getTotalMovies()).isEqualTo(2);
        assertThat(secondActor.getActorRating()).isEqualByComparingTo("8.0");
        assertThat(secondActor.getRankInSystem()).isEqualTo(2);
        assertThat(secondActor.getPerformanceStatus()).isEqualTo("Below Average");
    }
}