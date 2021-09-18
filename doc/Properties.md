- `spring.kafka.bootstrap-servers` - URL of bootstrap-servers 
(см. документацию кафки)
- `novus.messaging.channel.web.message-lifetime` - время в секундах, по истечении которого
сообщения не будут показываться, если пользователь не был подключен
(по умолчанию 60)
- `novus.messaging.durable` - должны ли подписки быть durable;
- `novus.messaging.topic.notice` - topic очереди сообщений для рассылки уведомлений пользователям в центр уведомлений системы (по умолчанию `novus-messaging-notify`)
- `novus.messaging.queue.status` - имя очереди статусов отправки уведомлений (по умолчанию `novus-messaging-status`)
- `novus.messaging.queue.feed-count` - имя очереди количества непрочитанных уведомлений (по умолчанию `novus-messaging-feed`)
- `novus.messaging.auth-header-name` - название для заголовка для аутентификации
и авторизации (по умолчанию `X-Auth-Token`)
