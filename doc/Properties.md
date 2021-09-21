- `spring.kafka.bootstrap-servers` - URL of bootstrap-servers 
(см. документацию кафки)
- `novus.messaging.timeout` - время в секундах, по истечении которого
сообщения не будут показываться, если пользователь не был подключен
(по умолчанию 60)
- `novus.messaging.durable` - должны ли подписки быть durable;
- `novus.messaging.notice.topic` - topic очереди сообщений для рассылки уведомлений пользователям в центр уведомлений системы (по умолчанию `novus-messaging-notify`)
- `novus.messaging.status.queue` - имя очереди статусов отправки уведомлений (по умолчанию `novus-messaging-status`)
- `novus.messaging.feed.queue` - имя очереди количества непрочтенных уведомлений (по умолчанию `novus-messaging-feed`)
- `novus.messaging.auth-header-name` - название для заголовка для аутентификации
и авторизации (по умолчанию `X-Auth-Token`)
