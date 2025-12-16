package dev.studentpp1.streamingservice.movies.repository;

import dev.studentpp1.streamingservice.AbstractPostgresContainerTest;
import dev.studentpp1.streamingservice.movies.entity.Actor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class ActorRepositoryTest extends AbstractPostgresContainerTest {

    @Autowired
    private ActorRepository actorRepository;

    @Test
    void shouldSaveAndFindActor() {
        Actor actor = new Actor();
        actor.setName("Cillian");
        actor.setSurname("Murphy");
        actor.setBiography("Irish actor");

        Actor savedActor = actorRepository.saveAndFlush(actor);

        Optional<Actor> foundActor = actorRepository.findById(savedActor.getId());

        assertThat(foundActor).isPresent();
        assertThat(foundActor.get().getSurname()).isEqualTo("Murphy");
    }
}