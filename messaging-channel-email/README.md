# Канал для отправки уведомлений через Email

## Подключение

1. Добавьте следующую зависимость

```xml
<dependency>
    <groupId>ru.i-novus.messaging</groupId>
    <artifactId>messaging-channel-email</artifactId>
    <version>${messaging.version}</version>
</dependency>
```

2. Добавьте скрипт в БД messaging со следующей строкой:

```roomsql
INSERT INTO messaging.channel (name, queue_name, is_internal, tenant_code) 
       VALUES ('Email', 'email-queue', false, 'my_tenant')
```

- `name` - Имя канала, отображаемое на UI
- `queue_name` - Имя очереди канала (должно совпадать с настройкой `novus.messaging.channel.email.queue`)
- `is_internal` - Признак того, что канал является внутрисистемным
- `my_tenant` - Код тенанта, к которому относится канал (по умолчанию использовать `default`)

3. Подключите аннотацию `@EnableEmailChannel` к вашему приложению

```java
@SpringBootApplication
@EnableEmailChannel
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
```

## Настройки

- `novus.messaging.channel.email.queue` - имя очереди уведомлений email канала (по умолчанию: `email-queue`)
- `spring.mail.host=smtp.gmail.com`
- `spring.mail.port=587`
- `spring.mail.username=[username]`
- `spring.mail.password=[password]`
- `spring.mail.properties.mail.smtp.auth=true`
- `spring.mail.properties.mail.smtp.starttls.enable=true`
