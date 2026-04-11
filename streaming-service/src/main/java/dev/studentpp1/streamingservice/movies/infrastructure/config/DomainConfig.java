package dev.studentpp1.streamingservice.movies.infrastructure.config;

import dev.studentpp1.streamingservice.movies.domain.factory.ActorFactory;
import dev.studentpp1.streamingservice.movies.domain.factory.DirectorFactory;
import dev.studentpp1.streamingservice.movies.domain.factory.MovieFactory;
import dev.studentpp1.streamingservice.movies.domain.factory.PerformanceFactory;
import dev.studentpp1.streamingservice.movies.domain.repository.ActorRepository;
import dev.studentpp1.streamingservice.movies.domain.repository.DirectorRepository;
import dev.studentpp1.streamingservice.movies.domain.repository.MovieRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration("moviesDomainConfig")
public class DomainConfig {

    @Bean
    public ActorFactory actorFactory() {
        return new ActorFactory();
    }

    @Bean
    public DirectorFactory directorFactory() {
        return new DirectorFactory();
    }

    @Bean
    public MovieFactory movieFactory(DirectorRepository directorRepository) {
        return new MovieFactory(directorRepository);
    }

    @Bean
    public PerformanceFactory performanceFactory(MovieRepository movieRepository,
                                                 ActorRepository actorRepository) {
        return new PerformanceFactory(movieRepository, actorRepository);
    }
}

