package dev.studentpp1.streamingservice.subscription.infrastructure.adapter;

import dev.studentpp1.streamingservice.common.dto.PageResult;
import dev.studentpp1.streamingservice.subscription.domain.model.SubscriptionPlan;
import dev.studentpp1.streamingservice.subscription.domain.repository.SubscriptionPlanRepository;
import dev.studentpp1.streamingservice.subscription.infrastructure.entity.SubscriptionPlanEntity;
import dev.studentpp1.streamingservice.subscription.infrastructure.mapper.SubscriptionPlanPersistenceMapper;
import dev.studentpp1.streamingservice.subscription.infrastructure.repository.SubscriptionPlanJpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class SubscriptionPlanRepositoryAdapter implements SubscriptionPlanRepository {

    private final SubscriptionPlanJpaRepository jpaRepository;
    private final SubscriptionPlanPersistenceMapper mapper;

    public SubscriptionPlanRepositoryAdapter(
            SubscriptionPlanJpaRepository jpaRepository,
            SubscriptionPlanPersistenceMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public Optional<SubscriptionPlan> findById(Long id) {
        return jpaRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public Optional<SubscriptionPlan> findByName(String name) {
        return jpaRepository.findByName(name).map(mapper::toDomain);
    }

    @Override
    public Optional<SubscriptionPlan> findByIdWithMovies(Long id) {
        return jpaRepository.findWithMoviesById(id).map(mapper::toDomain);
    }

    @Override
    public Optional<SubscriptionPlan> findByIdWithLock(Long id) {
        return jpaRepository.findByIdWithLock(id).map(mapper::toDomain);
    }

    @Override
    public PageResult<SubscriptionPlan> findAll(int page, int size) {
        Page<SubscriptionPlanEntity> entityPage =
                jpaRepository.findAll(PageRequest.of(page, size));
        return toPageResult(entityPage);
    }

    @Override
    public PageResult<SubscriptionPlan> findAllByNameContaining(
            String search, int page, int size) {
        Page<SubscriptionPlanEntity> entityPage =
                jpaRepository.findAllByNameContainingIgnoreCase(
                        search, PageRequest.of(page, size));
        return toPageResult(entityPage);
    }

    @Override
    public SubscriptionPlan save(SubscriptionPlan domain) {
        SubscriptionPlanEntity entity = mapper.toEntity(domain);

        if (!domain.getMovieIds().isEmpty()) {
            entity.setMovieIds(domain.getMovieIds());
        }

        return mapper.toDomain(jpaRepository.save(entity));
    }

    @Override
    public void delete(SubscriptionPlan domain) {
        jpaRepository.findById(domain.getId())
                .ifPresent(jpaRepository::delete);
    }

    @Override
    public boolean existsByName(String name) {
        return jpaRepository.existsByName(name);
    }

    private PageResult<SubscriptionPlan> toPageResult(Page<SubscriptionPlanEntity> page) {
        return new PageResult<>(
                page.getContent().stream().map(mapper::toDomain).toList(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages()
        );
    }
}