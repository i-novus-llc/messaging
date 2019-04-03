- `spring.kafka.bootstrap-servers` - URL of bootstrap-servers 
(см. документацию кафки)
- `novus.messaging.timeout` - время в секундах, по истечении которого
сообщения не будут показываться, если пользователь не был подключен
(по умолчанию 60)
- `novus.messaging.durable` - должны ли подписки быть durable;
учитывается только для ActiveMQ (по умолчанию true)
- `novus.messaging.topic.notice` - topic очереди сообщений для рассылки уведомлений пользователям в центр уведомлений системы (по умолчанию `novus-messaging-notify`)
- `novus.messaging.topic.email` - topic очереди сообщений  для рассылки уведомлений на электронную почту 
(по умолчанию `novus-messaging-email`)
- `novus.messaging.auth-header-name` - название для заголовка для аутенификации
и авторизации (по умолчанию `X-Auth-Token`)
- `activemq.broker-url` - URL брокера ActiveMQ
