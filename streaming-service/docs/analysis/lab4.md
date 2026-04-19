# Аналіз лабораторної 4

## 1. Що змінилося порівняно з лабораторною 3

- У лабораторній 3 був CQS (`CommandHandler`/`QueryHandler`) без явної міжкомпонентної взаємодії подіями.
- У лабораторній 4 додано подієву комунікацію:
  - sync: `PaymentSucceeded`/`PaymentFailed` -> `SubscriptionPaymentListener`
  - async: `SubscriptionActivated`/`SubscriptionFailed` -> `SubscriptionNotificationListener`
- Для збереження зв'язку платежу і підписки додано подію `SubscriptionLinkedToPayment`.

## 2. Які побічні операції виділено

- **Реакція підписок на оплату**: створення/скасування підписки після статусу платежу.
- **Нотифікації**: повідомлення користувача про успіх/помилку підписки.

Окремі компоненти:

- `subscription.application.event.listener.SubscriptionPaymentListener` 
    + контракти: `PaymentSucceeded`, `PaymentFailed`).
- `subscription.application.event.listener.SubscriptionNotificationListener` 
    + порт `subscription.domain.port.SubscriptionNotification` 
    + контракти: `SubscriptionActivated`, `SubscriptionFailed`

## 3. Sync vs Async

### Час відповіді API

- Sync-шлях довший: бізнес-ланцюг виконується в одному потоці.
- Async-шлях коротший: API не чекає обробку нотифікацій.

### Поведінка при збоях

- Sync: збій у `SubscriptionPaymentListener` зриває обробку основного write-flow.
- Async: збій у `SubscriptionNotificationListener` не відкочує вже завершену бізнес-операцію.

### Зв'язаність

- Немає прямого виклику між контекстами; взаємодія через типізовані події.
- Залежність на канал нотифікацій ізольована портом `SubscriptionNotification`.

## 4. Що обрано для production

- Обрано **гібрид**:
  - sync для критичних бізнес-наслідків (платіж => підписка);
  - async для побічних ефектів (нотифікації).

Причина: баланс між узгодженістю домену і швидкою відповіддю API.

## 5. Ідемпотентність

- `PaymentWebhookCommandHandler` ігнорує дублікати за статусом (`COMPLETED`/`FAILED`).
- `CancelSubscriptionAfterPaymentFailureHandler` не скасовує повторно неактивні підписки.
- `PaymentSubscriptionLinkListener` ідемпотентний для однакового `subscriptionId`.

## 6. Підсумок

- Вимоги лабораторної виконано: є окремі допоміжні компоненти, sync і async комунікація, події в past tense, покриття тестами.
- DDD/CQS збережено: домен без залежностей на Spring/HTTP/ORM, orchestration у `application`, контракти через порти та події.

