# Опис таблиць

## Діаграма сутність-зв'язок (ERD)

![ER diagram](https://raw.githubusercontent.com/markOone/StreamingService_BD/refs/heads/main/lab1/updated_diagram.jpg)

## Таблиця: `users`

**Призначення:** Зберігає інформацію про облікові записи користувачів стрімінгового сервісу

**Стовпці:**

| Стовпець | Тип | Обмеження | Опис |
|----------|-----|-----------|------|
| user_id | SERIAL | PRIMARY KEY | Унікальний ідентифікатор користувача |
| name | VARCHAR(100) | NOT NULL | Ім'я користувача |
| surname | VARCHAR(100) | NOT NULL | Прізвище користувача |
| email | VARCHAR(255) | UNIQUE, NOT NULL | Email для автентифікації |
| password | VARCHAR(255) | NOT NULL | Хешований пароль (bcrypt) |
| birthday | DATE | NOT NULL | Дата народження користувача |
| role | VARCHAR(20) | NOT NULL, DEFAULT 'USER' | Роль (USER/ADMIN) |
| deleted | BOOLEAN | NOT NULL, DEFAULT FALSE | Прапорець м'якого видалення |

**Індекси:**
- `idx_users_email_deleted` на `email WHERE deleted = FALSE` (для автентифікації non-deleted користувачів)

**Зв'язки:**
- Один-до-багатьох з `user_subscription` (користувач може мати кілька підписок)

---

## Таблиця: `subscription_plan`

**Призначення:** Зберігає тарифні плани підписки (Basic, Premium, тощо)

**Стовпці:**

| Стовпець | Тип | Обмеження | Опис                                  |
|----------|-----|-----------|---------------------------------------|
| subscription_plan_id | SERIAL | PRIMARY KEY | Унікальний ідентифікатор плану        |
| name | VARCHAR(150) | UNIQUE, NOT NULL | Назва плану (наприклад, "Basic Plan") |
| description | TEXT | NOT NULL | Детальний опис можливостей плану      |
| price | DECIMAL(8, 2) | NOT NULL, CHECK (price >= 0.0) | Вартість підписки                     |
| duration | INT | NOT NULL | Тривалість підписки в днях            |
| deleted | BOOLEAN | NOT NULL, DEFAULT FALSE | Прапорець м'якого видалення           |
| version | BIGINT | NOT NULL, DEFAULT 0 | Версія для оптимістичного блокування  |

**Індекси:**
- `idx_subscription_plan_name_unique_active` на `name WHERE deleted = FALSE` (унікальність назви тільки для активних планів)

**Зв'язки:**
- Один-до-багатьох з `user_subscription` (план може мати багато активних підписок)
- Багато-до-багатьох з `movie` через `included_movie` (план включає певні фільми)

---

## Таблиця: `user_subscription`

**Призначення:** Зберігає активні та історичні підписки користувачів

**Стовпці:**

| Стовпець | Тип | Обмеження | Опис |
|----------|-----|-----------|------|
| user_subscription_id | SERIAL | PRIMARY KEY | Унікальний ідентифікатор підписки |
| start_time | TIMESTAMP | NOT NULL | Час початку підписки |
| end_time | TIMESTAMP | NOT NULL | Час закінчення підписки |
| status | subscription_status | NOT NULL | Статус (ACTIVE/CANCELLED/EXPIRED) |
| subscription_plan_id | INT | NOT NULL, FK | Посилання на тарифний план |
| user_id | INT | NOT NULL, FK | Посилання на користувача |

**Обмеження:**
- `check_subscription_dates`: `end_time > start_time`

**Індекси:**
- `idx_user_subscription_user_status` на `(user_id, status)` (для пошуку підписок користувача)
- `idx_user_subscription_subscription_plan_id` на `subscription_plan_id` (для JOIN з планами)
- `idx_user_subscription_status_dates` на `(status, end_time DESC)` (для пошуку активних/завершених)

**Зв'язки:**
- Багато-до-одного з `users` (FK: user_id)
- Багато-до-одного з `subscription_plan` (FK: subscription_plan_id)
- Один-до-багатьох з `payment` (підписка може мати кілька платежів)

---

## Таблиця: `payment`

**Призначення:** Зберігає інформацію про всі платежі користувачів, включно з ініційованими (PENDING), успішними (COMPLETED) та неуспішними (FAILED). 
Таблиця використовується для історії оплат, аналітики та забезпечення коректної обробки конкурентних платіжних подій.

### **Стовпці:**

| Стовпець               | Тип            | Обмеження                    | Опис                                                                        |
| ---------------------- | -------------- | ---------------------------- |-----------------------------------------------------------------------------|
| `payment_id`           | SERIAL         | PRIMARY KEY                  | Унікальний ідентифікатор платежу                                            |
| `provider_session_id`  | VARCHAR(255)   | UNIQUE, NULL                 | Зовнішній ідентифікатор платежу у платіжного провайдера                     |
| `created_at`           | TIMESTAMP      | NOT NULL, DEFAULT NOW()      | Час створення платежу (момент ініціації оплати)                             |
| `paid_at`              | TIMESTAMP      | NULL                         | Час успішного завершення платежу; заповнюється лише для статусу `COMPLETED` |
| `amount`               | DECIMAL(10, 2) | NOT NULL, CHECK (amount > 0) | Сума платежу                                                                |
| `status`               | payment_status | NOT NULL                     | Статус платежу (`PENDING`, `COMPLETED`, `FAILED`, `REFUNDED`)               |
| `user_subscription_id` | INT            | NULL, FK                     | Посилання на підписку користувача; встановлюється після успішної оплати     |

### **Індекси:**

* `uk_payment_provider_pid` -- унікальний індекс на `provider_session_id WHERE provider_session_id IS NOT NULL`
  *(забезпечує ідемпотентність обробки платіжних подій)*
* `ix_payment_status_created` на `(status, created_at)`
  *(для пошуку та очищення завислих платежів зі статусом `PENDING`)*
* `idx_payment_user_subscription_id_status_amount` на `(user_subscription_id, status) INCLUDE (amount)`
  *(для JOIN-операцій з user_subscription та аналітики доходів)*
* `idx_payment_status_paid_at` на `(status, paid_at DESC)`
  *(для аналітичних запитів з агрегацією за датою)*
* `idx_payment_paid_at` на `paid_at`
  *(для видалення старих платежів)*

### **Зв'язки:**

* **Багато-до-одного** з таблицею `user_subscription`
  (`payment.user_subscription_id → user_subscription.user_subscription_id`)
  -- зв'язок встановлюється **після успішної оплати**.

---

## Таблиця: `movie`

**Призначення:** Зберігає каталог фільмів стрімінгового сервісу

**Стовпці:**

| Стовпець | Тип | Обмеження | Опис                                 |
|----------|-----|-----------|--------------------------------------|
| movie_id | SERIAL | PRIMARY KEY | Унікальний ідентифікатор фільму      |
| title | VARCHAR(255) | NOT NULL | Назва фільму                         |
| description | TEXT | NULL | Опис сюжету фільму                   |
| year | INT | NOT NULL, CHECK (year > 1878) | Рік випуску (після винаходу кіно)    |
| rating | DECIMAL(4, 1) | CHECK (rating >= 0.0 AND rating <= 10.0) | Рейтинг фільму (0-10)                |
| version | BIGINT | NOT NULL, DEFAULT 0 | Версія для оптимістичного блокування |
| director_id | INT | NOT NULL, FK | Посилання на режисера                |

**Індекси:**
- `idx_movie_director_id` на `director_id` (для пошуку фільмів режисера)

**Зв'язки:**
- Багато-до-одного з `director` (FK: director_id)
- Один-до-багатьох з `performance` (фільм має багато акторських ролей)
- Багато-до-багатьох з `subscription_plan` через `included_movie`

---

## Таблиця: `director`

**Призначення:** Зберігає інформацію про режисерів фільмів

**Стовпці:**

| Стовпець | Тип | Обмеження | Опис |
|----------|-----|-----------|------|
| director_id | SERIAL | PRIMARY KEY | Унікальний ідентифікатор режисера |
| name | VARCHAR(100) | NOT NULL | Ім'я режисера |
| surname | VARCHAR(100) | NOT NULL | Прізвище режисера |
| biography | TEXT | NULL | Біографія режисера |

**Зв'язки:**
- Один-до-багатьох з `movie` (режисер має багато фільмів)

---

## Таблиця: `actor`

**Призначення:** Зберігає інформацію про акторів

**Стовпці:**

| Стовпець | Тип | Обмеження | Опис |
|----------|-----|-----------|------|
| actor_id | SERIAL | PRIMARY KEY | Унікальний ідентифікатор актора |
| name | VARCHAR(100) | NOT NULL | Ім'я актора |
| surname | VARCHAR(100) | NOT NULL | Прізвище актора |
| biography | TEXT | NULL | Біографія актора |

**Зв'язки:**
- Один-до-багатьох з `performance` (актор може грати багато ролей)

---

## Таблиця: `performance`

**Призначення:** Зберігає інформацію про ролі акторів у фільмах (зв'язує акторів і фільми)

**Стовпці:**

| Стовпець | Тип | Обмеження | Опис |
|----------|-----|-----------|------|
| performance_id | SERIAL | PRIMARY KEY | Унікальний ідентифікатор ролі |
| character_name | VARCHAR(255) | NOT NULL | Ім'я персонажа |
| description | TEXT | NOT NULL | Опис ролі |
| actor_id | INT | NOT NULL, FK | Посилання на актора |
| movie_id | INT | NOT NULL, FK | Посилання на фільм |

**Індекси:**
- `idx_performance_actor_id` на `actor_id` (для фільмографії актора)
- `idx_performance_movie_id` на `movie_id` (для акторського складу фільму)

**Зв'язки:**
- Багато-до-одного з `actor` (FK: actor_id)
- Багато-до-одного з `movie` (FK: movie_id)

---

## Таблиця: `included_movie`

**Призначення:** Зв'язкова таблиця many-to-many між фільмами та підписками (які фільми доступні в якому плані)

**Стовпці:**

| Стовпець | Тип | Обмеження | Опис |
|----------|-----|-----------|------|
| movie_id | INT | PRIMARY KEY (composite), FK | Посилання на фільм |
| subscription_plan_id | INT | PRIMARY KEY (composite), FK | Посилання на план підписки |

**Індекси:**
- `idx_included_movie_subscription_plan_id` на `subscription_plan_id` (для пошуку фільмів плану)
- `idx_included_movie_movie_id` на `movie_id` (для reverse join — пошук планів для фільму, аналітика)
- Composite PRIMARY KEY автоматично створює індекс

**Зв'язки:**
- Багато-до-одного з `movie` (FK: movie_id)
- Багато-до-одного з `subscription_plan` (FK: subscription_plan_id)

---

## Структура схеми

**Обрана структура:** Реляційна модель з нормалізацією до 3НФ і використанням зв'язкових таблиць для багато-до-багатьох відносин.

**Чому саме така структура:**
1. **Модульність:** Кожна сутність (користувачі, фільми, платежі) виділена в окрему таблицю
2. **Розширюваність:** Легко додати нові тарифні плани або фільми без зміни схеми
3. **Аналітика:** Окрема таблиця `payment` дозволяє детальну аналітику платежів незалежно від статусу підписок
4. **Історичність:** `user_subscription` зберігає всю історію підписок користувача (не тільки активні)

## Рівень нормалізації

**Досягнута 3НФ (Третя нормальна форма):**

+ **1НФ:** Всі атрибути атомарні, немає повторюваних груп
- Наприклад, `name` і `surname` розділені
- Немає багатозначних атрибутів

+ **2НФ:** Немає часткових функціональних залежностей
- Всі не-ключові атрибути повністю залежать від первинного ключа
- У `included_movie` обидва поля складають композитний ключ

+ **3НФ:** Немає транзитивних залежностей
- `payment` посилається на `user_subscription`, а не дублює `user_id`
- Інформація про режисера не зберігається в таблиці `movie`, а винесена в `director`

## Компроміси

**1. М'яке видалення для users:**
- **Причина:** збереження історії платежів для подальшої аналітики

**2. М'яке видалення для subscription_plan:**
- **Причина:** не можемо просто видалити план підписки, бо користувачі вже заплатили за нього гроші

**3. Не зберігаємо user_id в payment:**
- **Причина:** payment прив’язується до конкретної підписки, а користувача можна отримати через `user_subscription_id`

## Стратегія індексування

**Принципи вибору індексів:**

1. **Foreign Keys:**
   - Всі FK отримали індекси для ефективних JOIN операцій
   - Використання INCLUDE для покриваючих індексів (наприклад, `idx_payment_user_subscription_id_status_amount`)

2. **Популярні запити:**
   - Автентифікація: `idx_users_email_deleted` (часткові індекс тільки для `deleted = FALSE`)
   - Аналітика: `idx_payment_status_paid_at` (для WHERE + GROUP BY при обчисленні доходів)
   - Пошук активних підписок: `idx_user_subscription_user_status` (композитний індекс)

3. **Часткові (Partial) індекси:**
   - `idx_users_email_deleted` - індексує лише активних користувачів
   - `uk_payment_provider_pid` - індексує лише записи з непустим provider_session_id
   - `idx_subscription_plan_name_unique_active` - унікальність назви тільки для невидалених планів

4. **Оптимізація для конкретних сценаріїв:**
   - Cleanup старих pending платежів: `ix_payment_status_created`
   - Reverse joins для аналітики: `idx_included_movie_movie_id`
