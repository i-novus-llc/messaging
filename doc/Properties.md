- `spring.kafka.consumer.group-id` - groupId для consumer-ов; для общих
сообщений будет использоваться именно эта группа, а для пользовательских
сообщений будет суффикс `-<userId>` (по умолчанию `novus-messaging`)
- `novus.messaging.timeout` - время в секундах, по истечении которого
сообщения не будут показываться, если пользователь не был подключен
(по умолчанию 60)
- `novus.messaging.topic` - topic для kafka, который будет использоваться
сервисом уведомлений (по умолчанию `novus-messaging-notify`)
- `novus.messaging.auth-header-name` - название для заголовка для аутенификации
и авторизации (по умолчанию `X-Auth-Token`)
