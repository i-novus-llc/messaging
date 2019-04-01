- `spring.kafka.bootstrap-servers` - URL of bootstrap-servers 
(см. документацию кафки)
- `novus.messaging.timeout` - время в секундах, по истечении которого
сообщения не будут показываться, если пользователь не был подключен
(по умолчанию 60)
- `novus.messaging.durable` - должны ли подписки быть durable;
учитывается только для ActiveMQ (по умолчанию true)
- `novus.messaging.topic` - topic для kafka, который будет использоваться
сервисом уведомлений (по умолчанию `novus-messaging-notify`)
- `email.topic` - topic для рассылки сообщений на электронную почту 
(по умолчанию `novus-messaging-email`)
- `novus.messaging.auth-header-name` - название для заголовка для аутенификации
и авторизации (по умолчанию `X-Auth-Token`)
- `activemq.broker-url` - URL брокера ActiveMQ
