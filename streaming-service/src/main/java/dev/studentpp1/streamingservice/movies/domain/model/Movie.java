package dev.studentpp1.streamingservice.movies.domain.model;

import dev.studentpp1.streamingservice.movies.domain.exception.InvalidRatingException;
import dev.studentpp1.streamingservice.movies.domain.exception.MovieDomainException;

import java.math.BigDecimal;
import java.time.Year;

public class Movie {
    private final Long id;
    private String title;
    private String description;
    private int year;
    private BigDecimal rating;
    private Long directorId;
    private Long version;

    private Movie(Long id, String title, String description,
                  int year, BigDecimal rating, Long directorId, Long version) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.year = year;
        this.rating = rating;
        this.directorId = directorId;
        this.version = version;
    }

    public static Movie restore(Long id, String title, String description,
                                int year, BigDecimal rating, Long directorId, Long version) {
        return new Movie(id, title, description, year, rating, directorId, version);
    }

    public static Movie create(String title, String description,
                               int year, BigDecimal rating, Long directorId) {
        validate(title, year, rating);
        return new Movie(null, title, description, year, rating, directorId, null);
    }

    public void update(String title, String description,
                       int year, BigDecimal rating, Long directorId) {
        validate(title, year, rating);
        this.title = title;
        this.description = description;
        this.year = year;
        this.rating = rating;
        this.directorId = directorId;
    }

    private static void validate(String title, int year, BigDecimal rating) {
        if (title == null || title.isBlank())
            throw new MovieDomainException("Movie title cannot be blank") {};
        if (year < 1888 || year > Year.now().getValue() + 5)
            throw new MovieDomainException("Invalid movie year: " + year) {};
        if (rating.compareTo(BigDecimal.ZERO) < 0 ||
                rating.compareTo(BigDecimal.TEN) > 0)
            throw new InvalidRatingException(rating.doubleValue());
    }

    public Long getId() { return id; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public int getYear() { return year; }
    public BigDecimal getRating() { return rating; }
    public Long getDirectorId() { return directorId; }
    public Long getVersion() { return version; }
}