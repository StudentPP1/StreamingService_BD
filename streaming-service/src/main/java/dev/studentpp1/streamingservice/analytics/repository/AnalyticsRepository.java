package dev.studentpp1.streamingservice.analytics.repository;

import dev.studentpp1.streamingservice.analytics.dto.ActorAnalyticsStats; // Наш новий імпорт
import dev.studentpp1.streamingservice.analytics.dto.DirectorRevenueStats;
import dev.studentpp1.streamingservice.analytics.dto.MonthlyPlanStatisticProjection;
import dev.studentpp1.streamingservice.movies.entity.Movie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
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

    @Query(value = """
        WITH GlobalStats AS (
            SELECT AVG(m.rating) as global_avg_rating
            FROM movie m
        ),
        ActorStats AS (
            SELECT 
                a.actor_id,
                CONCAT(a.name, ' ', a.surname) as full_name,
                COUNT(DISTINCT m.movie_id) as total_movies,
                COUNT(DISTINCT d.director_id) as distinct_directors,
                AVG(m.rating) as avg_actor_rating
            FROM actor a
            JOIN performance p ON a.actor_id = p.actor_id
            JOIN movie m ON p.movie_id = m.movie_id
            JOIN director d ON m.director_id = d.director_id
            GROUP BY a.actor_id, a.name, a.surname
            HAVING COUNT(DISTINCT m.movie_id) >= 1
        )
        SELECT 
            s.full_name as fullName,
            s.total_movies as totalMovies,
            s.distinct_directors as distinctDirectors,
            CAST(s.avg_actor_rating AS NUMERIC(3,1)) as actorRating,
            DENSE_RANK() OVER (ORDER BY s.avg_actor_rating DESC) as rankInSystem,
            CASE 
                WHEN s.avg_actor_rating > g.global_avg_rating THEN 'Above Average'
                ELSE 'Below Average'
            END as performanceStatus
        FROM ActorStats s
        CROSS JOIN GlobalStats g
        ORDER BY s.avg_actor_rating DESC
        LIMIT 20
        """, nativeQuery = true)
    List<ActorAnalyticsStats> findActorAnalytics();

    @Query(value = """
                    WITH monthly_statistic AS (
                SELECT
                    DATE_TRUNC('month', p.paid_at)::date AS current_month,
                    sp.name                             AS plan_name,
                    COUNT(DISTINCT us.user_id)          AS unique_users,
                    COUNT(p.payment_id)                 AS payment_count,
                    SUM(p.amount)                       AS total_plan_amount
                FROM payment p
                JOIN user_subscription us
                    ON p.user_subscription_id = us.user_subscription_id
                JOIN subscription_plan sp
                    ON us.subscription_plan_id = sp.subscription_plan_id
                WHERE p.status = 'COMPLETED'
                GROUP BY current_month, plan_name
            )
            SELECT
                ms.current_month,
                ms.plan_name,
                ms.unique_users,
                ms.payment_count,
                ms.total_plan_amount,
                SUM(ms.total_plan_amount) OVER (PARTITION BY ms.current_month) AS month_sum,
                ROUND(
                    ms.total_plan_amount / SUM(ms.total_plan_amount) OVER (PARTITION BY ms.current_month) * 100,
                    2
                ) AS percent_in_total_sum
            FROM monthly_statistic ms
            ORDER BY ms.current_month DESC, ms.total_plan_amount DESC;
            """,
            nativeQuery = true
    )
    List<MonthlyPlanStatisticProjection> findMonthlyPlanStatistics();
}