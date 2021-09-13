- `spring.kafka.bootstrap-servers` - URL of bootstrap-servers 
(см. документацию кафки)
- `novus.messaging.timeout` - время в секундах, по истечении которого
сообщения не будут показываться, если пользователь не был подключен
(по умолчанию 60)
- `novus.messaging.durable` - должны ли подписки быть durable;
учитывается только для ActiveMQ (по умолчанию true)
- `novus.messaging.topic.notice` - topic очереди сообщений для рассылки уведомлений пользователям в центр уведомлений системы (по умолчанию `novus-messaging-notify`)
- `novus.messaging.status.queue` - имя очереди статусов отправки уведомлений (по умолчанию `novus-messaging-status`)
- `novus.messaging.auth-header-name` - название для заголовка для аутенификации
и авторизации (по умолчанию `X-Auth-Token`)
- `activemq.broker-url` - URL брокера ActiveMQ
- `novus.messaging.redelivery-policy.<очередь>` - настройки повторной отправки сообщений (только для очередей activeMQ) см. https://activemq.apache.org/redelivery-policy
    - `novus.messaging.redelivery-policy.<очередь>.initial-redelivery-delay` - интервал между первой отправкой сообщения и первой повторной отправкой сообщения, мс. (только для очередей activeMQ)
    - `novus.messaging.redelivery-policy.<очередь>.redelivery-delay` - интервал между последующими повторными отправками сообщения, мс. (только для очередей activeMQ)
    - `novus.messaging.redelivery-policy.<очередь>.maximum-redeliveries` - число повторных отправок сообщения, не считая первую отправку сообщения (только для очередей activeMQ)
    - `novus.messaging.redelivery-policy.<очередь>.use-exponential-back-off` - изменять ли интервал повторной отправки сообщения (только для очередей activeMQ)
    - `novus.messaging.redelivery-policy.<очередь>.back-off-multiplier` - коэффициент изменения интервала повторной отправки сообщения (только для очередей activeMQ)
