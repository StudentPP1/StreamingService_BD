# Аналіз лабораторної 4

## Що змінилося порівняно з лабораторною 3
- Виділено окремий допоміжний компонент нотифікацій (`notification`).
- Для успішної оплати використано асинхронний сценарій: публікація `SubscriptionActivated` у `EventBus`.
- Для неуспішної оплати використано синхронний сценарій: прямий виклик `SubscriptionNotification.notifyFailed` через порт.

## Які побічні операції виділено і чому
1. Нотифікація про успішну оплату (не блокує основну операцію, тому асинхронно).
2. Нотифікація про неуспішну оплату (у поточній реалізації виконується синхронно в тому ж потоці).

## Порівняння синхронного та асинхронного підходів
### Час відповіді API
- Для асинхронного сценарію
  2026-04-24T00:15:44.328+03:00  INFO 21800 --- [nio-8081-exec-1] s.s.p.a.c.w.PaymentWebhookCommandHandler : Received Stripe event type=checkout.session.completed
  2026-04-24T00:15:44.355+03:00  INFO 21800 --- [nio-8081-exec-1] reateUserSubscriptionAfterPaymentHandler : Subscription created: userId=7, plan=Premium
  2026-04-24T00:15:44.357+03:00  INFO 21800 --- [onPool-worker-1] .s.s.n.a.SubscriptionNotificationAdapter : Subscription activated: email=mishamakytonin@gmail.com, plan=Premium, expires=2026-05-24T00:15:44.345813500
  2026-04-24T00:15:44.358+03:00  INFO 21800 --- [nio-8081-exec-1] s.s.p.a.c.w.PaymentWebhookCommandHandler : Payment processed successfully for sessionId=cs_test_a1mQWtxxI5Zk7LoM9AzFpeavENpUGVcJnnJsjWXs0zuIi2VfBJ8XaizOnc
  Не блокує основний потік, нотифікація відправляється в фоновому режимі після обробки платежу.

- Для синхронного сценарію
  2026-04-24T00:13:49.490+03:00  WARN 21800 --- [nio-8081-exec-2] .s.s.n.a.SubscriptionNotificationAdapter : Subscription failed: email=mishamakytonin@gmail.com, plan=Premium, reason=Payment attempt failed
  2026-04-24T00:14:01.244+03:00  INFO 21800 --- [nio-8081-exec-2] s.s.p.a.c.w.PaymentWebhookCommandHandler : Payment attempt FAILED via payment_intent for userId=7, plan=Premium
  Основний потік блокується до завершення нотифікації, що може збільшити latency і вплинути на користувацький досвід.

### Поведінка при збоях
- Асинхронно: збій у обробнику не ламає основну операцію (перевірено тестом на виняток у listener).
- Синхронно: виняток із `notifyFailed(...)` йде вгору (перевірено в `onPaymentFailed_propagatesNotificationError_syncBehavior`).

### Зв'язаність між компонентами
- Асинхронний варіант зменшує зв'язаність: модулі взаємодіють через подію.
- Синхронний варіант підвищує зв'язаність: є прямий виклик контракту нотифікації.

### Складність реалізації та тестування
- Асинхронний підхід складніший у відлагодженні та тестуванні (потрібно враховувати фонове виконання).
- Синхронний підхід простіший для трасування, але гірший за ізоляцією збоїв і latency.

## Який підхід обрано для production і чому
Для побічних ефектів (нотифікацій) пріоритетний асинхронний підхід, бо він не блокує основний бізнес-флоу.
У поточній реалізації використано змішаний варіант: success -> async, failed -> sync.

## Що буде при повторній доставці тієї самої події (ідемпотентність)
- Повторна обробка частково стримується на рівні payment-flow (перевірки статусів/стану платежу).
- Окремого dedup-механізму в notification-компоненті немає, тому при повторній доставці можливе повторне надсилання повідомлення.