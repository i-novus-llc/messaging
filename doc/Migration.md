# Руководство по миграции

#### 4.1.1

* Следующие настройки больше не используются

```
novus.messaging.role-provider-url
```

* Следующие настройки были переименованы

```    
 - `novus.messaging.user-role-provider` -> `novus.messaging.recipient-provider.type`
 - `novus.messaging.user-provider-url` -> `novus.messaging.recipient-provider.url`
 - `novus.messaging.security-admin-url` -> `novus.messaging.recipient-provider.url`
 - `novus.messaging.mapping-file-location` -> `novus.messaging.recipient-provider.configurable.mapping-file-location`
```

* Изменена структура файла для маппинга полей в конфигурируемом провайдере получателей:
    * удалена возможность указания ролей
    * `<user>` переименован в `<recipient>`

* Изменения в Rest API:
    - `/user` -> `/provider_recipients`
    - `/{tenantCode}/settings` -> `/{tenantCode}/templates`
    - убрана мультитенантность у каналов: `/{tenantCode}/channels` -> `/channels`
* Прекращена поддержка пользовательских шаблонов уведомлений

#### 4.1.0

* Прекращена поддержка компонентов системы
* Добавлена мультитенантность
    - `systemId` изменено на `tenantCode`
    - Изменения в Rest API:
        - `/messages` -> `/{tenantCode}/messages`
        - `/settings` -> `/{tenantCode}/settings`
        - `/user/{username}/settings` -> `/{tenantCode}/user/{username}/settings`
        - `/feed` -> `/{tenantCode}/feed`
        - `/channels` -> `/{tenantCode}/channels`
        - `/recipients` -> `/{tenantCode}/recipients`

#### 4.0.0

* Прекращена поддержка ActiveMQ. Необходимо использовать Kafka.
* Следующие настройки больше не используются

```
activemq.broker-url
novus.messaging.redelivery-policy.<очередь>`
novus.messaging.redelivery-policy.<очередь>.initial-redelivery-delay
novus.messaging.redelivery-policy.<очередь>.redelivery-delay
novus.messaging.redelivery-policy.<очередь>.maximum-redeliveries
novus.messaging.redelivery-policy.<очередь>.use-exponential-back-off
novus.messaging.redelivery-policy.<очередь>.back-off-multiplier
novus.messaging.auth-header-name
novus.messaging.durable
```

* Следующие настройки были переименованы
    - `messaging.user-role-provider` -> `novus.messaging.user-role-provider`
    - `messaging.mapping-file-location` -> `novus.messaging.mapping-file-location`
    - `messaging.user-provider-url` -> `novus.messaging.user-provider-url`
    - `messaging.role-provider-url` -> `novus.messaging.role-provider-url`
    - `novus.messaging.topic.email` -> `novus.messaging.channel.email.queue` (перенесена в
      модуль `messaging-channel-email`)
    - `novus.messaging.timeout` -> `novus.messaging.channel.web.message-lifetime` (эта и следующие настройки перенесены
      в модуль `messaging-channel-web`)
    - `novus.messaging.topic.notice` -> `novus.messaging.channel.web.topic`
    - `novus.messaging.app_prefix` -> `novus.messaging.channel.web.app_prefix`
    - `novus.messaging.end_point` -> `novus.messaging.channel.web.end_point`
    - `novus.messaging.public_dest_prefix` -> `novus.messaging.channel.web.public_dest_prefix`
    - `novus.messaging.private_dest_prefix` -> `novus.messaging.channel.web.private_dest_prefix`
* Удален `InfoType` enum. Теперь информация о каналах содержится в БД. А сами каналы подключаются через зависимости.
* Изменен механизм отправки уведомлений в канал. Теперь одно уведомление можно отправлять только в один канал. Раньше
  можно было отправлять в несколько каналов одновременно.
* В `messaging-channel-web` (канал передачи уведомлений в web через web socket) осталась только базовая аутентификация.
  Механизм аутентификации в web теперь определяет сам пользователь.

#### 3.39

* Изменены названия настроек для задания публичного ключа в сервисе messaging-backend
    - `novus.messaging.keycloak.modulus` -> `novus.messaging.jwt.verifier-key.modulus`
    - `novus.messaging.keycloak.exponent` -> `novus.messaging.jwt.verifier-key.exponent`
