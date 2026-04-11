package dev.studentpp1.streamingservice.movies.application.cqs;

import dev.studentpp1.streamingservice.movies.application.cqs.MoviesCqs.*;
import dev.studentpp1.streamingservice.movies.application.usecase.ActorService;
import dev.studentpp1.streamingservice.movies.application.usecase.DirectorService;
import dev.studentpp1.streamingservice.movies.application.usecase.MovieService;
import dev.studentpp1.streamingservice.movies.application.usecase.PerformanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MoviesCommandHandler {
    private final MovieService movieService;
    private final ActorService actorService;
    private final DirectorService directorService;
    private final PerformanceService performanceService;

    public Long handle(CreateMovieCommand command) {
        return movieService.createMovie(command.request()).getId();
    }

    public Long handle(UpdateMovieCommand command) {
        return movieService.updateMovie(command.id(), command.request()).getId();
    }

    public void handle(DeleteMovieCommand command) {
        movieService.deleteMovie(command.id());
    }

    public Long handle(CreateActorCommand command) {
        return actorService.createActor(command.request()).getId();
    }

    public Long handle(UpdateActorCommand command) {
        return actorService.updateActor(command.id(), command.request()).getId();
    }

    public void handle(DeleteActorCommand command) {
        actorService.deleteActor(command.id());
    }

    public Long handle(CreateDirectorCommand command) {
        return directorService.createDirector(command.request()).getId();
    }

    public Long handle(UpdateDirectorCommand command) {
        return directorService.updateDirector(command.id(), command.request()).getId();
    }

    public void handle(DeleteDirectorCommand command) {
        directorService.deleteDirector(command.id());
    }

    public Long handle(CreatePerformanceCommand command) {
        return performanceService.createPerformance(command.request()).getId();
    }

    public void handle(DeletePerformanceCommand command) {
        performanceService.deletePerformance(command.id());
    }
}
