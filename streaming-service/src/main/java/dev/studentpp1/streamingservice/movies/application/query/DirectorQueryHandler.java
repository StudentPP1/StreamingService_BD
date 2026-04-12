package dev.studentpp1.streamingservice.movies.application.query;

import dev.studentpp1.streamingservice.common.dto.PageResult;
import dev.studentpp1.streamingservice.movies.application.usecase.DirectorService;
import dev.studentpp1.streamingservice.movies.domain.model.Director;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DirectorQueryHandler {

    private final DirectorService directorService;

    public PageResult<Director> handle(GetAllDirectorsQuery query) {
        return directorService.getAllDirectors(query.page(), query.size());
    }

    public Director handle(GetDirectorByIdQuery query) {
        return directorService.getDirectorById(query.id());
    }

    public DirectorService.DirectorWithMovies handle(GetDirectorDetailsQuery query) {
        return directorService.getDirectorDetails(query.id());
    }
}

