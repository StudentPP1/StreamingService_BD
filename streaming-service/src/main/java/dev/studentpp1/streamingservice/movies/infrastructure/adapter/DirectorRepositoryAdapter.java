package dev.studentpp1.streamingservice.movies.infrastructure.adapter;

import dev.studentpp1.streamingservice.movies.domain.model.Director;
import dev.studentpp1.streamingservice.common.dto.PageResult;
import dev.studentpp1.streamingservice.movies.domain.repository.DirectorRepository;
import dev.studentpp1.streamingservice.movies.infrastructure.entity.DirectorEntity;
import dev.studentpp1.streamingservice.movies.infrastructure.mapper.DirectorPersistenceMapper;
import dev.studentpp1.streamingservice.movies.infrastructure.repository.DirectorJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class DirectorRepositoryAdapter implements DirectorRepository {

    private final DirectorJpaRepository jpaRepository;
    private final DirectorPersistenceMapper mapper;

    @Override
    public Optional<Director> findById(Long id) {
        return jpaRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public List<Director> findAll() {
        return jpaRepository.findAll().stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Director save(Director director) {
        return mapper.toDomain(jpaRepository.save(mapper.toEntity(director)));
    }


    @Override
    public void deleteById(Long id) {
        jpaRepository.deleteById(id);
    }

    @Override
    public boolean existsById(Long id) {
        return jpaRepository.existsById(id);
    }

    @Override
    public PageResult<Director> findAll(int page, int size) {
        Page<DirectorEntity> entityPage = jpaRepository.findAll(PageRequest.of(page, size));
        return new PageResult<>(
                entityPage.getContent().stream().map(mapper::toDomain).toList(),
                entityPage.getNumber(),
                entityPage.getSize(),
                entityPage.getTotalElements(),
                entityPage.getTotalPages()
        );
    }
}
