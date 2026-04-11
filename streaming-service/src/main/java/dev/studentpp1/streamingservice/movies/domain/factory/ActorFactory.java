package dev.studentpp1.streamingservice.movies.domain.factory;

import dev.studentpp1.streamingservice.movies.domain.model.Actor;

public class ActorFactory {

    public Actor create(String name, String surname,
                        String biography) {
        return Actor.create(name, surname, biography);
    }
}