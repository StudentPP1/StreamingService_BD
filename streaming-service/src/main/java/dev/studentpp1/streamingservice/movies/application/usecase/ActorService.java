package dev.studentpp1.streamingservice.movies.application.usecase;

import dev.studentpp1.streamingservice.movies.domain.exception.ActorNotFoundException;
import dev.studentpp1.streamingservice.movies.domain.exception.MovieNotFoundException;
import dev.studentpp1.streamingservice.movies.domain.factory.ActorFactory;
import dev.studentpp1.streamingservice.movies.domain.model.Actor;
import dev.studentpp1.streamingservice.movies.domain.model.Movie;
import dev.studentpp1.streamingservice.common.dto.PageResult;
import dev.studentpp1.streamingservice.movies.domain.model.Performance;
import dev.studentpp1.streamingservice.movies.domain.repository.ActorRepository;
import dev.studentpp1.streamingservice.movies.domain.repository.MovieRepository;
import dev.studentpp1.streamingservice.movies.domain.repository.PerformanceRepository;
import dev.studentpp1.streamingservice.movies.presentation.dto.request.ActorCreateRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ActorService {

    private final ActorRepository actorRepository;
    private final MovieRepository movieRepository;
    private final PerformanceRepository performanceRepository;
    private final ActorFactory actorFactory;

    @Transactional(readOnly = true)
    public Actor getActorById(Long id) {
        return actorRepository.findById(id)
                .orElseThrow(() -> new ActorNotFoundException(id));
    }

    @Transactional(readOnly = true)
    public ActorDetails getActorDetails(Long id) {
        Actor actor = actorRepository.findById(id)
                .orElseThrow(() -> new ActorNotFoundException(id));
        List<Performance> performances = performanceRepository.findByActorId(id);
        List<ActorFilmographyItem> filmography = performances.stream()
                .map(p -> {
                    Movie movie = movieRepository.findById(p.getMovieId())
                            .orElseThrow(() -> new MovieNotFoundException(p.getMovieId()));
                    return new ActorFilmographyItem(p, movie);
                })
                .toList();
        return new ActorDetails(actor, filmography);
    }

    @Transactional
    public Actor createActor(ActorCreateRequest request) {
        Actor actor = actorFactory.create(
                request.name(),
                request.surname(),
                request.biography()
        );
        return actorRepository.save(actor);
    }

    @Transactional
    public Actor updateActor(Long id, ActorCreateRequest request) {
        Actor actor = actorRepository.findById(id)
                .orElseThrow(() -> new ActorNotFoundException(id));

        actor.update(request.name(), request.surname(), request.biography());

        return actorRepository.save(actor);
    }

    @Transactional
    public void deleteActor(Long id) {
        if (!actorRepository.existsById(id)) {
            throw new ActorNotFoundException(id);
        }
        actorRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public PageResult<Actor> getAllActors(int page, int size) {
        return actorRepository.findAll(page, size);
    }


    public record ActorDetails(Actor actor, List<ActorFilmographyItem> filmography) {
    }

    public record ActorFilmographyItem(Performance performance, Movie movie) {
    }
}