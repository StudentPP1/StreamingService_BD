package dev.studentpp1.streamingservice.movies.infrastructure.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "performance")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PerformanceEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "performance_id")
    private Long id;

    @Column(name = "character_name", nullable = false, length = 255)
    private String characterName;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @ManyToOne
    @JoinColumn(name = "actor_id", nullable = false)
    private ActorEntity actorEntity;

    @ManyToOne
    @JoinColumn(name = "movie_id", nullable = false)
    private MovieEntity movieEntity;
}