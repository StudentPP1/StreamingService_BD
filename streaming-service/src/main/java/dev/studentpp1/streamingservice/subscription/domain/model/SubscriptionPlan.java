package dev.studentpp1.streamingservice.subscription.domain.model;

import dev.studentpp1.streamingservice.subscription.domain.exception.SubscriptionDomainException;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class SubscriptionPlan {
    private final Long id;
    private String name;
    private String description;
    private BigDecimal price;
    private Integer duration;
    private Set<Long> movieIds;
    private final Long version;

    private SubscriptionPlan(Long id, String name, String description,
                             BigDecimal price, Integer duration,
                             Set<Long> movieIds, Long version) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.duration = duration;
        this.movieIds = new HashSet<>(movieIds);
        this.version = version;
    }

    public static SubscriptionPlan restore(Long id, String name, String description,
                                           BigDecimal price, Integer duration,
                                           Set<Long> movieIds, Long version) {
        return new SubscriptionPlan(id, name, description, price, duration, movieIds, version);
    }

    public static SubscriptionPlan create(String name, String description,
                                          BigDecimal price, Integer duration) {
        validate(name, description, price, duration);
        return new SubscriptionPlan(null, name, description, price, duration, Set.of(), null);
    }

    public void update(String name, String description, BigDecimal price, Integer duration) {
        validate(name, description, price, duration);
        this.name = name;
        this.description = description;
        this.price = price;
        this.duration = duration;
    }

    private static void validate(String name, String description,
                                 BigDecimal price, Integer duration) {
        if (name == null || name.isBlank())
            throw new SubscriptionDomainException("Plan name cannot be blank");
        if (description == null || description.isBlank())
            throw new SubscriptionDomainException("Plan description cannot be blank");
        if (price == null || price.compareTo(BigDecimal.ZERO) < 0)
            throw new SubscriptionDomainException("Plan price cannot be negative");
        if (duration == null || duration < 1)
            throw new SubscriptionDomainException("Plan duration must be at least 1 day");
    }

    public Long getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public BigDecimal getPrice() { return price; }
    public Integer getDuration() { return duration; }
    public Long getVersion() { return version; }
    public Set<Long> getMovieIds() { return Collections.unmodifiableSet(movieIds); }

    public void setMovieIds(Set<Long> movieIds) {
        this.movieIds.clear();
        this.movieIds.addAll(movieIds);
    }

    public void addMovies(Set<Long> validatedIds) {
        this.movieIds.addAll(validatedIds);
    }

    public boolean removeMovies(Set<Long> movieIds) {
        return this.movieIds.removeAll(movieIds);
    }
}