package dev.studentpp1.streamingservice.movies.application.usecase;

import dev.studentpp1.streamingservice.movies.domain.exception.PerformanceNotFoundException;
import dev.studentpp1.streamingservice.movies.domain.factory.PerformanceFactory;
import dev.studentpp1.streamingservice.movies.domain.model.Performance;
import dev.studentpp1.streamingservice.movies.domain.repository.PerformanceRepository;
import dev.studentpp1.streamingservice.movies.application.dto.PerformanceCreateRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PerformanceService {

    private final PerformanceRepository performanceRepository;
    private final PerformanceFactory performanceFactory;

    public PerformanceService(PerformanceRepository performanceRepository,
                              PerformanceFactory performanceFactory) {
        this.performanceRepository = performanceRepository;
        this.performanceFactory = performanceFactory;
    }

    @Transactional(readOnly = true)
    public Performance getPerformanceById(Long id) {
        return performanceRepository.findById(id)
                .orElseThrow(() -> new PerformanceNotFoundException(id));
    }

    @Transactional(readOnly = true)
    public List<Performance> getActorPerformances(Long id) {
        return performanceRepository.findByActorId(id);
    }

    @Transactional
    public Performance createPerformance(PerformanceCreateRequest request) {
        Performance performance = performanceFactory.create(
                request.movieId(),
                request.actorId(),
                request.characterName(),
                request.description()
        );
        return performanceRepository.save(performance);
    }

    @Transactional
    public void deletePerformance(Long id) {
        if (!performanceRepository.existsById(id)) {
            throw new PerformanceNotFoundException(id);
        }
        performanceRepository.deleteById(id);
    }
}