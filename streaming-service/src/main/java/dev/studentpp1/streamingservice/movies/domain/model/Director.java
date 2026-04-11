package dev.studentpp1.streamingservice.movies.domain.model;

import dev.studentpp1.streamingservice.movies.domain.exception.MovieDomainException;

public class Director {
    private final Long id;
    private String name;
    private String surname;
    private String biography;

    private Director(Long id, String name, String surname, String biography) {
        this.id = id;
        this.name = name;
        this.surname = surname;
        this.biography = biography;
    }

    public static Director restore(Long id, String name, String surname, String biography) {
        return new Director(id, name, surname, biography);
    }

    public static Director create(String name, String surname, String biography) {
        validate(name, surname);
        return new Director(null, name, surname, biography);
    }

    public void update(String name, String surname, String biography) {
        validate(name, surname);
        this.name = name;
        this.surname = surname;
        this.biography = biography;
    }

    private static void validate(String name, String surname) {
        if (name == null || name.isBlank())
            throw new MovieDomainException("Director name cannot be blank") {};
        if (surname == null || surname.isBlank())
            throw new MovieDomainException("Director surname cannot be blank") {};
    }

    public Long getId() { return id; }
    public String getName() { return name; }
    public String getSurname() { return surname; }
    public String getBiography() { return biography; }
}