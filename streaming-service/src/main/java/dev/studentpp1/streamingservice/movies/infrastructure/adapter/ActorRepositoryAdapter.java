package dev.studentpp1.streamingservice.movies.infrastructure.adapter;

import dev.studentpp1.streamingservice.movies.domain.model.Actor;
import dev.studentpp1.streamingservice.common.dto.PageResult;
import dev.studentpp1.streamingservice.movies.domain.repository.ActorRepository;
import dev.studentpp1.streamingservice.movies.infrastructure.entity.ActorEntity;
import dev.studentpp1.streamingservice.movies.infrastructure.mapper.ActorPersistenceMapper;
import dev.studentpp1.streamingservice.movies.infrastructure.repository.ActorJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ActorRepositoryAdapter implements ActorRepository {

    private final ActorJpaRepository jpaRepository;
    private final ActorPersistenceMapper mapper;

    @Override
    public Optional<Actor> findById(Long id) {
        return jpaRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public List<Actor> findAll() {
        return jpaRepository.findAll().stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Actor save(Actor actor) {
        return mapper.toDomain(jpaRepository.save(mapper.toEntity(actor)));
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
    public PageResult<Actor> findAll(int page, int size) {
        Page<ActorEntity> entityPage = jpaRepository.findAll(PageRequest.of(page, size));
        return new PageResult<>(
                entityPage.getContent().stream().map(mapper::toDomain).toList(),
                entityPage.getNumber(),
                entityPage.getSize(),
                entityPage.getTotalElements(),
                entityPage.getTotalPages()
        );
    }
}
