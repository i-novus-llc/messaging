# Канал для отправки уведомлений через Web Socket

## Настройки
`novus.messaging.channel.web.app_prefix` - префикс пути приложения 
(по умолчанию: `/app`)

`novus.messaging.channel.web.end_point` - эндпоинт, по которому регистрируется поддержка STOMP 
(по умолчанию: `/ws`)

`novus.messaging.channel.web.public_dest_prefix` - публичный префикс пути клиента 
(по умолчанию: `/topic`)

`novus.messaging.channel.web.private_dest_prefix` - приватный префикс пути клиента 
(по умолчанию: `/exchange`)

`novus.messaging.channel.web.message-lifetime` - время в секундах, по истечении которого
сообщения не будут показываться, если пользователь не был подключен (по умолчанию: `60`)

`novus.messaging.channel.web.queue` - имя очереди сообщений веб канала (по умолчанию: `web-queue`)

`novus.messaging.channel.web.topic` - имя топика сообщений веб канала (по умолчанию: `web-notice-topic`)

## Формат сообщений для Web Socket

### Соединение
```json
{ 
  "type" : "CONNECT", 
  "headers" : { 
    "X-Auth-Token" : "user-name-token", 
    "X-System-Id" : "system-id"
  }
}
```

Хедер `X-System-Id` нужен для того, чтобы можно было использовать один и тот же сервис
сообщений с разными системами. В МТР это разные региональные системы 
и центральный. Все хедеры являются обязательными. Без них сервис не сможет
идентифицировать клиента.

Описание `X-Auth-Token` см. в разделе [Security](../doc/Security.md).

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