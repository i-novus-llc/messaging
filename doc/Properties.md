# Настройки сервиса уведомлений

- `novus.messaging.queue.status` - имя очереди статусов отправки уведомлений (по умолчанию `novus-messaging-status`)
- `novus.messaging.queue.feed-count` - имя очереди количества непрочитанных уведомлений (по умолчанию `novus-messaging-feed`)
- `novus.messaging.tenant-code` - код текущего тенанта (по умолчанию `default`)

## Настройки провайдеров

- `novus.messaging.recipient-provider.type` - провайдер данных для получения пользователей и ролей. Доступные
  значения `configurable` или `security` (по умолчанию: `configurable`)
- `novus.messaging.recipient-provider.url` - адрес, с которого провайдер будет забирать информацию о получателях
- `novus.messaging.recipient-provider.configurable.mapping-file-location` - путь до файла с маппингом полей 
  для конфигурируемого провайдера (по умолчанию: `classpath:recipientProviderFieldMapping.xml`)

## Настройки Email канала

- `novus.messaging.channel.email.queue` - имя очереди уведомлений email канала (по умолчанию: `email-queue`)
- `novus.messaging.channel.email.from` - имя отправителя (по умолчанию: `spring.mail.username`)

## Настройки Web канала

- `novus.messaging.channel.web.app_prefix` - префикс пути приложения
  (по умолчанию: `/app`)
- `novus.messaging.channel.web.end_point` - эндпоинт, по которому регистрируется поддержка STOMP
  (по умолчанию: `/ws`)
- `novus.messaging.channel.web.public_dest_prefix` - публичный префикс пути клиента
  (по умолчанию: `/topic`)
- `novus.messaging.channel.web.private_dest_prefix` - приватный префикс пути клиента
  (по умолчанию: `/exchange`)
- `novus.messaging.channel.web.message-lifetime` - время в секундах, по истечении которого уведомления не будут
  показываться, если пользователь не был подключен (по умолчанию: `60`)
- `novus.messaging.channel.web.queue` - имя очереди уведомлений веб канала (по умолчанию: `web-queue`)
- `novus.messaging.channel.web.topic` - имя топика уведомлений веб канала (по умолчанию: `web-topic`)
