# Канал для отправки уведомлений через Web Socket

## Подключение
1. Добавьте следующую зависимость
```xml
<dependency>
    <groupId>ru.i-novus.messaging</groupId>
    <artifactId>messaging-channel-web</artifactId>
    <version>${messaging.version}</version>
</dependency>
```
2. Добавьте скрипт в БД со следующей строкой:
```roomsql
INSERT INTO messaging.channel (id, name, queue_name, is_internal) 
       VALUES ('web', 'Web', 'web-queue', true)
```
- id - уникальный код канала
- name - имя канала, отображаемое на UI
- queue_name - имя очереди канала (должно совпадать с настройкой `novus.messaging.channel.web.queue`)
- is_internal - признак того, что канал является внутрисистемным 

## Настройки
- `novus.messaging.channel.web.app_prefix` - префикс пути приложения 
(по умолчанию: `/app`)

- `novus.messaging.channel.web.end_point` - эндпоинт, по которому регистрируется поддержка STOMP 
(по умолчанию: `/ws`)

- `novus.messaging.channel.web.public_dest_prefix` - публичный префикс пути клиента 
(по умолчанию: `/topic`)

- `novus.messaging.channel.web.private_dest_prefix` - приватный префикс пути клиента 
(по умолчанию: `/exchange`)

- `novus.messaging.channel.web.message-lifetime` - время в секундах, по истечении которого
сообщения не будут показываться, если пользователь не был подключен (по умолчанию: `60`)

- `novus.messaging.channel.web.queue` - имя очереди сообщений веб канала (по умолчанию: `web-queue`)

- `novus.messaging.channel.web.topic` - имя топика сообщений веб канала (по умолчанию: `web-notice-topic`)

## Формат сообщений для Web Socket

### Получение уведомления
`/user/{username}/exchange/{systemId}/message`
```json
{
  "id" : 2,
  "caption": "Заголовок сообщения",
  "text" : "Текст сообщения",
  "severity": {
    "value": 30,
    "name": "Ошибка"
  },
  "recipients": [
    {
      "name": "Joe",
      "username": "joe@gmail.com"
    }
  ]
}
```

### Получения количества непрочитанных уведомлений
`/user/{username}/exchange/{systemId}/message.count`
```json
{
  "value" : 3
}
```

### Подтверждение прочтения уведомления пользователем
`/app/{systemId}/message.markread`
```json
{ 
  "type" : "READ",
  "messageId": "e9f2969a-5ed1-485e-af86-2fe3c7d67073",
  "headers" : { 
    "X-Auth-Token" : "user-name-token"
  }
}
```

### Подтверждение прочтения всех уведомлений пользователем
`/app/{systemId}/message.markreadall`
```json
{ 
  "type" : "READ",
  "headers" : { 
    "X-Auth-Token" : "user-name-token"
  }
}
```