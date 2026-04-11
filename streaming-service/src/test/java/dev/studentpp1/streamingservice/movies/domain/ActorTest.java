package dev.studentpp1.streamingservice.movies.domain;

import dev.studentpp1.streamingservice.movies.domain.exception.MovieDomainException;
import dev.studentpp1.streamingservice.movies.domain.model.Actor;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class ActorTest {

    @Test
    void create_validActor_success() {
        Actor actor = Actor.create("Leonardo", "DiCaprio", "biography");
        assertThat(actor.getName()).isEqualTo("Leonardo");
        assertThat(actor.getId()).isNull();
    }

    @Test
    void create_blankName_throwsDomainException() {
        assertThatThrownBy(() -> Actor.create("", "DiCaprio", "bio"))
                .isInstanceOf(MovieDomainException.class);
    }

    @Test
    void create_blankSurname_throwsDomainException() {
        assertThatThrownBy(() -> Actor.create("Leonardo", "", "bio"))
                .isInstanceOf(MovieDomainException.class);
    }

    @Test
    void update_validData_success() {
        Actor actor = Actor.restore(1L, "Leonardo", "DiCaprio", "bio");
        actor.update("Brad", "Pitt", "new bio");
        assertThat(actor.getName()).isEqualTo("Brad");
    }
}