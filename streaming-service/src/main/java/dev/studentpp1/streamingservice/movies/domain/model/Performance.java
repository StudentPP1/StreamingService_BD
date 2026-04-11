package dev.studentpp1.streamingservice.movies.domain.model;

import dev.studentpp1.streamingservice.movies.domain.exception.MovieDomainException;

public class Performance {
    private final Long id;
    private final Long movieId;
    private final Long actorId;
    private String characterName;
    private String description;

    private Performance(Long id, Long movieId, Long actorId,
                        String characterName, String description) {
        this.id = id;
        this.movieId = movieId;
        this.actorId = actorId;
        this.characterName = characterName;
        this.description = description;
    }

    public static Performance restore(Long id, Long movieId, Long actorId,
                                      String characterName, String description) {
        return new Performance(id, movieId, actorId, characterName, description);
    }

    public static Performance create(Long movieId, Long actorId,
                                     String characterName, String description) {
        if (characterName == null || characterName.isBlank())
            throw new MovieDomainException("Character name cannot be blank") {};
        return new Performance(null, movieId, actorId, characterName, description);
    }

    public Long getId() { return id; }
    public Long getMovieId() { return movieId; }
    public Long getActorId() { return actorId; }
    public String getCharacterName() { return characterName; }
    public String getDescription() { return description; }
}