package dev.studentpp1.streamingservice.analytics.repository;

import dev.studentpp1.streamingservice.analytics.dto.DirectorRevenueStats;
import dev.studentpp1.streamingservice.analytics.dto.MonthlyPlanStatisticProjection;
import dev.studentpp1.streamingservice.movies.entity.Movie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AnalyticsRepository extends JpaRepository<Movie, Long> {

    @Query(value = """
            WITH DirectorRevenue AS (
                SELECT
                    d.director_id,
                    d.name || ' ' || d.surname as director_name,
                    sp.name as plan_name,
                    COUNT(DISTINCT p.payment_id) as payments_count,
                    SUM(p.amount) as total_generated_revenue
                FROM director d
                JOIN movie m ON d.director_id = m.director_id
                JOIN included_movie im ON m.movie_id = im.movie_id
                JOIN subscription_plan sp ON im.subscription_plan_id = sp.subscription_plan_id
                JOIN user_subscription us ON sp.subscription_plan_id = us.subscription_plan_id
                JOIN payment p ON us.user_subscription_id = p.user_subscription_id
                WHERE p.status = 'COMPLETED'
                GROUP BY d.director_id, d.name, d.surname, sp.name
                HAVING SUM(p.amount) > 0
            )
            SELECT
                director_name as directorName,
                plan_name as planName,
                total_generated_revenue as totalGeneratedRevenue,
                DENSE_RANK() OVER (ORDER BY total_generated_revenue DESC) as revenueRank
            FROM DirectorRevenue
            ORDER BY revenueRank ASC
            LIMIT 10
            """, nativeQuery = true)
    List<DirectorRevenueStats> findTopDirectorsByRevenue();

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