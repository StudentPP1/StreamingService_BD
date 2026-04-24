# Аналіз лабораторної 5

## 1. Як визначалися межі bounded contexts

У системі виділено п'ять ізольованих модулів:

- **users** — автентифікація, управління акаунтами.
- **movies** — каталог фільмів (актори, режисери).
- **payments** — обробка платежів через Stripe, зберігання статусів оплат.
- **subscription** — підписки користувачів, плани.
- **analytics** — read-only проєкції метрик (successful/failed payments, activated/failed subscriptions).
- **notification** — email-повідомлення.

Обрана межа: `payments` отримує результат від Stripe і оркеструє подальше, але не знає про деталі підписки - він використовує порт `SubscriptionAfterPaymentPort`. 
Підписка не знає про платіжну систему - вона отримує команду через той самий порт і відповідає своїм ідентифікатором.

Рішення зафіксовано в ADR `docs/adr/0002-modular-monolith-bounded-contexts.md`.

---

## 2. Модульна структура і публічні контракти

Кожен модуль має ізольовану внутрішню архітектуру `domain / application / infrastructure / presentation` та явний публічний контракт в пакеті `api`:

| Модуль | Публічний контракт |
|---|---|
| `payments` | `api.checkout.PaymentCheckoutApi`, `api.event.PaymentSucceededEvent`, `api.event.PaymentFailedEvent` |
| `subscription` | `api.event.SubscriptionActivatedEvent`, `api.event.SubscriptionFailedEvent` |
| `movies` | `api.query.MoviesQueryApi`, `api.query.MovieView` |
| `analytics` | `api.AnalyticsQueryApi`, `api.AnalyticsSummaryView` |

Правило: інший модуль може імпортувати лише класи з `*.api.*`. 
Прямий доступ до `domain`, `application` чи `infrastructure` іншого модуля заборонений. 

Порти для cross-module синхронної комунікації оголошуються в `domain.port` в модулі, який потребує зовнішнього функціоналу 
і реалізуються в `infrastructure.adapter` цього модуля, в якому імпортуються класи з `*.api.*` іншого модуля.

---

## 3. ACL: як захищаються модулі від змін чужих моделей

ACL реалізовано в кожному модулі-споживачі.

### ACL в Analytics

`analytics.internal.acl.AnalyticsAclTranslator` переводить чотири зовнішні події Core у внутрішні моделі аналітики:

```
PaymentSucceededEvent      → PaymentMetric(successful=true)
PaymentFailedEvent         → PaymentMetric(successful=false)
SubscriptionActivatedEvent → SubscriptionMetric(activated=true)
SubscriptionFailedEvent    → SubscriptionMetric(activated=false)
```

### ACL в Subscription

`subscription.internal.acl.SubscriptionMoviesAclTranslator` перетворює `MovieView` у внутрішній `SubscriptionMovie`

`subscription.internal.acl.SubscriptionPaymentsAclTranslator` транслює внутрішній `CheckoutCommand` → `PaymentCheckoutRequest` і `PaymentCheckoutResponse` → внутрішній `CheckoutResult`. 

---

## 4. Міжмодульна комунікація: де порти, де події

### Ключовий принцип: порти для бізнес-логіки, події для побічних ефектів

**Порти (синхронна, транзакційна комунікація):**

- `SubscriptionAfterPaymentPort` — `PaymentWebhookCommandHandler` синхронно викликає порт при webhook від Stripe. У цей момент в одній `@Transactional` транзакції:
  1. Платіж позначається як COMPLETED/FAILED.
  2. Підписка створюється або скасовується.
  3. `payment.userSubscriptionId` оновлюється.

  Якщо підписку не вдалося створити — транзакція відкочується. Стан `payment` і `subscription` завжди консистентний.

- `SubscriptionPaymentGateway` — підписка ініціює Stripe Checkout через платіжний порт, не знаючи деталей реалізації.

- `MovieProvider` — підписка отримує список фільмів для плану через порт (використовуючи ACL-транслятор).

**Події (асинхронна eventual consistency):**

Після того як транзакція завершилася успішно, публікуються події виключно для побічних ефектів:

| Подія | Хто публікує | Хто споживає | Мета |
|---|---|---|---|
| `SubscriptionActivatedEvent` | `SubscriptionAfterPaymentPortAdapter` | `AnalyticsEventsListener`, `SubscriptionNotificationListener` | аналітика + email |
| `SubscriptionFailedEvent` | `SubscriptionAfterPaymentPortAdapter` | `AnalyticsEventsListener`, `SubscriptionNotificationListener` | аналітика + email |
| `PaymentSucceededEvent` | `PaymentWebhookCommandHandler` | `AnalyticsEventsListener` | аналітика |
| `PaymentFailedEvent` | `PaymentWebhookCommandHandler` | `AnalyticsEventsListener` | аналітика |

### Де strong consistency

- **Всередині одного модуля** — операції в межах одного `@Transactional` виклику (наприклад, оновлення стану `Payment` і збереження в БД).
- **Між `payments` і `subscription` через порт** — один `@Transactional` метод у `PaymentWebhookCommandHandler` охоплює і payments, і subscription операцію через порт. Це свідоме рішення: неприпустимо мати ситуацію, коли платіж позначений COMPLETED, але підписка не створена.

### Де eventual consistency

- **Core → Analytics**: аналітичні лічильники оновлюються асинхронно. Допустимо, бо dashboard аналітики — read-only представлення для адмінів, короткочасна затримка в лічильниках не критична.
- **Core → Notification**: email-повідомлення надсилається асинхронно після транзакції. Затримка в кілька секунд між оплатою і листом прийнятна для бізнесу.

---

## 5. Що зміниться, якщо виділити Analytics в окремий сервіс

`analytics` — найкращий кандидат на extraction, бо вже повністю ізольований:

- явний публічний контракт `analytics.api.*`;
- отримує дані виключно через події, не викликає жодних портів Core;
- ACL вже локалізовано всередині модуля;
- власна модель даних (метрики, проєкції), що не залежить від Core entity.

**Що треба змінити технічно:**

1. Замінити event-bus на зовнішній брокер (Kafka/RabbitMQ): Core публікує події в топік, Analytics підписується.
2. Вирішити питання persistence: зараз аналітика тримає стан у `AnalyticsData`. При виділенні потрібна власна БД (або Redis).
3. Додати retry для надійної доставки подій.

**Що не доведеться переробляти:**

- Доменну логіку Core — вона не знає, хто слухає події.
- Структуру ACL у Analytics — принцип трансляції подій у метрики залишається.
- Публічні контракти (events structure) — вони вже стабілізовані в `api.event`.

---

## 6. Ретроспектива курсу: від ЛР1 до ЛР5

### ЛР1 — моноліт без шарів

Логіка була розмішана між контролерами, сервісами і репозиторіями. Жодних явних доменних інваріантів — валідація розкидана де попало. Будь-яка нова вимога торкалася одразу кількох класів. Тести або не існували, або тестували HTTP-шар з підключеною БД.

### ЛР2 — шарова архітектура + доменна модель

Появилося чітке розділення: контролер → application-сервіс → доменна модель → репозиторний порт → інфраструктурний адаптер. Бізнес-правила стали жити в domain-класах (фабрики, value objects, методи агрегату). Репозиторні порти дозволили тестувати application-шар без БД. Це виявилося найціннішою структурною інвестицією — всі наступні рефакторинги спиралися на цю основу.

### ЛР3 — CQS

Команди і запити розділено. Перевага — запити можна оптимізувати (нативний SQL, проєкції) незалежно від команд. Тестування write/read-сценаріїв стало ізольованим. Контролери стали тонкими делегаторами. Незначний downside: більше файлів (окремий handler на кожен юзкейс), але кожен файл читається за хвилину.

### ЛР4 — async комунікація

Впроваджено `EventBus` . Порівняно sync і async сценарії: async notification не блокує основний запит, збої в listener не ламають транзакцію. 

### ЛР5 — modular monolith + bounded contexts + ACL

Кожен модуль став ізольованим з публічним контрактом. 
Events тільки для notification і analytics — там eventual consistency прийнятна. 
ACL захистив кожен модуль від змін чужих моделей.

---

## 7. Найцінніші архітектурні рішення

1. **Репозиторний порт як абстракція** (з ЛР2) — дозволив підміняти інфраструктуру без зміни domain/application шарів і ізолювати unit-тести.
2. **CQS** (з ЛР3) — розділення write/read зробило кожен handler одновідповідальним, з можливістю незалежної оптимізації читання.
3. **Порт для синхронної cross-module бізнес-логіки** (ЛР5) — payments і subscription консистентні в одній транзакції через порт.
4. **ACL у споживачах** — зміни в публічному контракті одного модуля ніколи не розповсюджуються по всьому споживачу, а локалізуються в ACL-транслятор.

---

## 8. Що зробили б інакше, знаючи кінцевий результат

- Відразу почав би з імплементації DDD, а не простого controller-service-repository. 
- Це б заощадило час на рефакторинги між ЛР1 і ЛР2.

---

## 9. Trade-offs: простота проти гнучкості

| Що ускладнилося | Що стало краще |
|---|---|
| Більше файлів: контракти, ACL, адаптери, порти | Зміна реалізації модуля не ламає споживачів |
| Більший mental overhead | Кожен модуль розуміється незалежно |
| Складніший debugging | Межі відповідальності очевидні, проблема локалізується швидше |
| Більше mapping-шарів  | Кожен шар незалежно тестується, еволюціонує |

---

## 10. Висновок

За п'ять лабораторних система еволюціонувала від процедурного моноліту до модульного моноліту з явними межами, портами, ACL і подієвою eventual consistency там, де вона виправдана. Кожне структурне рішення вирішувало конкретну проблему попереднього етапу. Головний висновок: архітектурні кордони не обмежують — вони знижують довгострокову вартість змін.
