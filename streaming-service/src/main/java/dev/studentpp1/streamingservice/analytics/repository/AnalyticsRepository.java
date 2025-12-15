package dev.studentpp1.streamingservice.analytics.repository;

import dev.studentpp1.streamingservice.analytics.dto.DirectorRevenueStats;
import dev.studentpp1.streamingservice.movies.entity.Movie;
import java.time.LocalDateTime;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AnalyticsRepository extends JpaRepository<Movie, Long> {

    @Query(value = """
        WITH RevenuePerPlan AS (
                    SELECT
                        d.director_id,
                        d.name || ' ' || d.surname as director_name,
                        sp.name as plan_name,
                        SUM(p.amount) as revenue
                    FROM director d
                    JOIN movie m USING(director_id)
                    JOIN included_movie im USING(movie_id)
                    JOIN subscription_plan sp USING(subscription_plan_id)
                    JOIN user_subscription us USING(subscription_plan_id)
                    JOIN payment p USING(user_subscription_id)
                    WHERE p.status = 'COMPLETED'
                            AND p.paid_at >= :startDate AND p.paid_at <= :endDate
                    GROUP BY d.director_id, sp.subscription_plan_id
                    HAVING SUM(p.amount) > 0
                )
                SELECT
                    director_name as directorName,
                    SUM(revenue) as totalRevenue,
                    STRING_AGG(plan_name, ', ') as planNames,
                    CAST(json_object_agg(plan_name, revenue) AS TEXT) as revenueBreakdownJson,
                    DENSE_RANK() OVER (ORDER BY SUM(revenue) DESC) as revenueRank
                FROM RevenuePerPlan
                GROUP BY director_name
                ORDER BY revenueRank
                LIMIT 10
        """, nativeQuery = true)
    List<DirectorRevenueStats> findTopDirectorsAggregated(
        @Param("startDate") LocalDateTime startDate,
        @Param("endDate") LocalDateTime endDate
    );
}