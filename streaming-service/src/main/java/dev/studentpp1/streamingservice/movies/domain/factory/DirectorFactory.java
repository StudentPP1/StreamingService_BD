package dev.studentpp1.streamingservice.movies.domain.factory;

import dev.studentpp1.streamingservice.movies.domain.model.Director;

public class DirectorFactory {

    public Director create(String name, String surname, String biography) {
        return Director.create(name, surname, biography);
    }
}