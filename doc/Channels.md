# Каналы отправки уведомлений

Сервис предоставляет возможность использовать различные каналы доставки уведомлений пользователям.

Главное правило: одно уведомление не может быть отправлено более чем в один канал.

## Cписок доступных каналов по умолчанию

По умолчанию присутствуют следующие реализации каналов доставки:

- [Email](../messaging-channel-email/README.md)
- [Web через Web Socket](../messaging-channel-web/README.md)

Изначально ни один из этих каналов не подключен. 
Вы можете сами выбрать какой из них (или оба) подходит под ваши цели. 

С подключением каналов можете познакомиться в соответствующих разделах их документации

## Механизм работы каналов с очередями

В данном сервисе все каналы получают уведомления из очередей, управляемых kafka. 
Для каждого канала существует отдельная очередь, которая не должна пересекаться с очередями других каналов.

Имена очередей каналов задаются с помощью настроек. 
Так, например, для email канала это `novus.messaging.channel.email.queue`.

После попытки отправки уведомления каналом, будь то успех или провал, статус уведомления добавляется в очередь статусов.
Очередь статусов является общей для всех каналов и задается настройкой `novus.messaging.queue.status`.

## Подключение собственной реализации канала

Если приведенные выше каналы не удовлетворяют вашим целям,
то сервис предоставляет возможность добавить свои собственные реализации каналов.

1. Для этого создайте модуль с вашей реализацией и добавьте следующую зависимость

```xml
<dependency>
    <groupId>ru.i-novus.messaging</groupId>
    <artifactId>messaging-channel-api</artifactId>
    <version>${messaging.version}</version>
</dependency>
```

2. Создайте класс, отвечающий за реализацию канала, и сделайте его наследником AbstractChannel

```java
public class MyChannel extends AbstractChannel {

    public MyChannel(String messageQueueName,
                     String statusQueueName,
                     MqProvider mqProvider) {
        super(mqProvider, messageQueueName, statusQueueName);
        ...
    }

    // Отправка уведомления в канал
    public void send(Message message) {
        ...
    }
}
```

Необходимо учесть, что `statusQueueName` - это имя очереди статусов уведомлений
и оно должно совпадать с настройкой `novus.messaging.queue.status`.

`messageQueueName` - имя очереди, из которой в ваш канал будут приходить уведомления (например: `my-channel-queue`).

3. Для подключения необходимо добавить скрипт в БД messaging со следующей строкой:

 ```roomsql
 INSERT INTO messaging.channel (code, name, queue_name, is_internal, tenant_code) 
        VALUES ('my_ch', 'My Channel', 'my-channel-queue', false)
 ```

- `code` - Уникальный код канала
- `name` - Имя канала, отображаемое на UI
- `queue_name` - Имя очереди канала (`my-channel-queue`)
- `is_internal` - Признак того, что канал является внутрисистемным




