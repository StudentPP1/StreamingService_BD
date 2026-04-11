package dev.studentpp1.streamingservice.movies.application.cqs;
import dev.studentpp1.streamingservice.movies.presentation.dto.request.ActorCreateRequest;
import dev.studentpp1.streamingservice.movies.presentation.dto.request.DirectorCreateRequest;
import dev.studentpp1.streamingservice.movies.presentation.dto.request.MovieCreateRequest;
import dev.studentpp1.streamingservice.movies.presentation.dto.request.PerformanceCreateRequest;
public final class MoviesCqs {
    private MoviesCqs() {}
    public record GetAllMoviesQuery(int page, int size) {}
    public record GetMovieByIdQuery(Long id) {}
    public record GetMovieDetailsQuery(Long id) {}
    public record CreateMovieCommand(MovieCreateRequest request) {}
    public record UpdateMovieCommand(Long id, MovieCreateRequest request) {}
    public record DeleteMovieCommand(Long id) {}
    public record GetAllActorsQuery(int page, int size) {}
    public record GetActorByIdQuery(Long id) {}
    public record GetActorDetailsQuery(Long id) {}
    public record CreateActorCommand(ActorCreateRequest request) {}
    public record UpdateActorCommand(Long id, ActorCreateRequest request) {}
    public record DeleteActorCommand(Long id) {}
    public record GetAllDirectorsQuery(int page, int size) {}
    public record GetDirectorByIdQuery(Long id) {}
    public record GetDirectorDetailsQuery(Long id) {}
    public record CreateDirectorCommand(DirectorCreateRequest request) {}
    public record UpdateDirectorCommand(Long id, DirectorCreateRequest request) {}
    public record DeleteDirectorCommand(Long id) {}
    public record GetPerformanceByIdQuery(Long id) {}
    public record CreatePerformanceCommand(PerformanceCreateRequest request) {}
    public record DeletePerformanceCommand(Long id) {}
}
