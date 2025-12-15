package dev.studentpp1.streamingservice.subscription.entity;

import dev.studentpp1.streamingservice.movies.entity.Movie;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "subscription_plan")
@SQLDelete(sql = "UPDATE subscription_plan SET deleted = true WHERE subscription_plan_id = ?")
@SQLRestriction("deleted = false")
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class SubscriptionPlan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "subscription_plan_id")
    @EqualsAndHashCode.Include
    private Long id;

    @NotBlank(message = "Name is required")
    @Size(max = 150, message = "Name must be less than 150 chars")
    @Column(nullable = false, unique = true, length = 150)
    private String name;

    @NotBlank(message = "Description is required")
    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.0", message = "Price cannot be negative")
    @Digits(integer = 6, fraction = 2)
    @Column(nullable = false, precision = 8, scale = 2)
    private BigDecimal price;

    @NotNull
    @Min(value = 1, message = "Duration must be at least 1 day")
    @Column(nullable = false)
    private Integer duration;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "included_movie",
        joinColumns = @JoinColumn(name = "subscription_plan_id"),
        inverseJoinColumns = @JoinColumn(name = "movie_id")
    )
    @Builder.Default
    private Set<Movie> movies = new HashSet<>();

    @Version
    private Long version;
}
