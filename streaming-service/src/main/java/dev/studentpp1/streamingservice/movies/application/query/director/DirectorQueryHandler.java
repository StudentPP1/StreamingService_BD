package dev.studentpp1.streamingservice.movies.application.query.director;

import dev.studentpp1.streamingservice.common.dto.PageResult;
import dev.studentpp1.streamingservice.movies.domain.exception.DirectorNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DirectorQueryHandler {

    private final DirectorReadRepository directorReadRepository;

    public PageResult<DirectorReadModel> handle(GetAllDirectorsQuery query) {
        return directorReadRepository.findAll(query.page(), query.size());
    }

    public DirectorReadModel handle(GetDirectorByIdQuery query) {
        return directorReadRepository.findById(query.id())
                .orElseThrow(() -> new DirectorNotFoundException(query.id()));
    }

    public DirectorDetailsReadModel handle(GetDirectorDetailsQuery query) {
        return directorReadRepository.findDetailsById(query.id())
                .orElseThrow(() -> new DirectorNotFoundException(query.id()));
    }
}
