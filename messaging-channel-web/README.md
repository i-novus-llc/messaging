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

2. Добавьте скрипт в БД messaging со следующей строкой:

```roomsql
INSERT INTO messaging.channel (code, name, queue_name, is_internal, tenant_code) 
       VALUES ('web', 'Web', 'web-queue', true, 'my_tenant')
```

- `code` - Уникальный код канала
- `name` - Имя канала, отображаемое на UI
- `queue_name` - Имя очереди канала (должно совпадать с настройкой `novus.messaging.channel.web.queue`)
- `is_internal` - Признак того, что канал является внутрисистемным
- `my_tenant` - Код тенанта, к которому относится канал (по умолчанию использовать `default`)

3. Подключите аннотацию `@EnableWebChannel` к вашему приложению

```java
@SpringBootApplication
@EnableWebChannel
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
```

## Настройки

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

## Формат сообщений для Web Socket

### Получение уведомления

`/user/{username}/exchange/{systemId}/message`

```json
{
  "id": 2,
  "caption": "Заголовок уведомления",
  "text": "Текст уведомления",
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

### Получение количества непрочитанных уведомлений

`/user/{username}/exchange/{systemId}/message.count`

```json
{
  "value": 3
}
```

### Подтверждение прочтения уведомления пользователем

`/app/{systemId}/message.markread`

```json
{
  "type": "READ",
  "messageId": "e9f2969a-5ed1-485e-af86-2fe3c7d67073",
  "headers": {
    "X-Auth-Token": "user-name-token"
  }
}
```

### Подтверждение прочтения всех уведомлений пользователем

`/app/{systemId}/message.markreadall`

```json
{
  "type": "READ",
  "headers": {
    "X-Auth-Token": "user-name-token"
  }
}
```