# Запит 1: Щомісячний дохід за планами підписок

## Бізнес-питання
+ Які підписки найкраще продаються?
+ Які підписки слід змінити чи видалити (бо нерентабельні)?

## SQL-запит
```sql
WITH monthly_statistic AS (
    SELECT
        DATE_TRUNC('month', p.paid_at)::date AS current_month,
        sp.name                              AS plan_name,
        COUNT(DISTINCT us.user_id)           AS unique_users,
        COUNT(p.payment_id)                  AS payment_count,
        SUM(p.amount)                        AS total_plan_amount
    FROM payment p
             JOIN user_subscription us
                  ON p.user_subscription_id = us.user_subscription_id
             JOIN subscription_plan sp
                  ON us.subscription_plan_id = sp.subscription_plan_id
    WHERE p.status = 'COMPLETED'
    GROUP BY
        current_month,
        plan_name
)

SELECT
    ms.current_month,
    ms.plan_name,
    ms.unique_users,
    ms.payment_count,
    ms.total_plan_amount,
    SUM(ms.total_plan_amount) OVER (
        PARTITION BY ms.current_month
    ) AS month_sum,
    ROUND(
            ms.total_plan_amount
                / SUM(ms.total_plan_amount) OVER (PARTITION BY ms.current_month)
        * 100,
            2
    ) AS percent_in_total_sum
FROM monthly_statistic ms
ORDER BY
    ms.current_month DESC,
    ms.total_plan_amount DESC;
```

## Пояснення
- JOIN таблиць payment, user_subscription, subscription_plan
- Групування за місяцем та ім'ям плану
- Обчислення загального доходу для плану та загальний за місяць
- Фільтрація лише завершених оплат
- Сортування результатів хронологічно та за зростанням вартості

## Приклад виводу:

| current\_month | plan\_name | unique\_users | payment\_count | total\_plan\_amount | month\_sum | percent\_in\_total\_sum |
| :--- | :--- | :--- | :--- | :--- | :--- | :--- |
| 2025-12-01 | STANDARD | 2 | 2 | 240 | 340 | 70.59 |
| 2025-12-01 | BASIC | 1 | 1 | 100 | 340 | 29.41 |

---

# Запит 2: Топ-10 режисерів за доходом

## Бізнес-питання
+ Які режисери приносять найбільший дохід компанії?
+ Як розподілений дохід режисерів за різними підписками?
+ Хто з режисерів найбільш популярний серед користувачів?

## SQL-запит
```sql
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
LIMIT 10;
```

## Пояснення
- CTE (RevenuePerPlan) обчислює дохід для кожного режисера по кожному плану підписки
- JOIN таблиць director → movie → included_movie → subscription_plan → user_subscription → payment
- Фільтрація за періодом часу (startDate, endDate) та статусом оплати (COMPLETED)
- Агрегація доходів по режисерах з використанням STRING_AGG для списку планів
- JSON_OBJECT_AGG для детального розподілу доходу по планах
- DENSE_RANK для рейтингу режисерів за загальним доходом
- LIMIT 10 для відображення тільки топ-10

## Приклад виводу:

| directorName | totalRevenue | planNames | revenueBreakdownJson                                                    | revenueRank |
| :--- |:-------------| :--- |:------------------------------------------------------------------------| :--- |
| Steven Spielberg | 424.94       | Premium, Annual, Standard | {"Annual": 399.96, "Premium": 14.99, "Standard": 9.99}                  | 1 |
| Sofia Coppola | 399.96       | Annual | {"Annual": 399.96}                                                      | 2 |
| Christopher Nolan | 23.95        | Standard, Batman Classics Collection, Basic | {"Standard": 9.99, "Batman Classics Collection": 8.97, "Basic": 4.99}   | 3 |
