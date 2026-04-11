package dev.studentpp1.streamingservice.movies.domain.model;

import dev.studentpp1.streamingservice.movies.domain.exception.MovieDomainException;

public class Actor {
    private final Long id;
    private String name;
    private String surname;
    private String biography;

    private Actor(Long id, String name, String surname,
                  String biography) {
        this.id = id;
        this.name = name;
        this.surname = surname;
        this.biography = biography;
    }

    public static Actor restore(Long id, String name, String surname,
                                String biography) {
        return new Actor(id, name, surname, biography);
    }

    public static Actor create(String name, String surname,
                               String biography) {
        validate(name, surname);
        return new Actor(null, name, surname, biography);
    }

    public void update(String name, String surname,
                       String biography) {
        validate(name, surname);
        this.name = name;
        this.surname = surname;
        this.biography = biography;
    }

    private static void validate(String name, String surname) {
        if (name == null || name.isBlank())
            throw new MovieDomainException("Actor name cannot be blank") {
            };
        if (surname == null || surname.isBlank())
            throw new MovieDomainException("Actor surname cannot be blank") {
            };
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getSurname() {
        return surname;
    }

    public String getBiography() {
        return biography;
    }
}