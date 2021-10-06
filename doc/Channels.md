# Каналы отправки уведомлений
Сервис предоставляет возможность использовать различные каналы доставки уведомлений пользователям.

Главное правило: одно уведомление не может быть отправлено более чем в один канал.

## Cписок доступных каналов по умолчанию
По умолчанию присутствуют следующие реализации каналов доставки:
- [Email](../messaging-channel-email/README.md)
- [Web через Web Socket](../messaging-channel-web/README.md)

Изначально ни один из этих каналов не подключен. 
Вы можете сами выбрать какой из них (или оба) подходит под ваши цели. 

## Подключение каналов по умолчанию
С подключением каналов по умолчанию можете познакомиться в соответствующих разделах их документации
- [Email](../messaging-channel-email/README.md#Подключение)
- [Web](../messaging-channel-web/README.md#Подключение)

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

    // Отправка сообщения в канал
    public void send(Message message) {
        ...
    }
}
```
Необходимо учесть, что `statusQueueName` - это имя очереди статусов уведомлений
и оно должно совпадать с настройкой `novus.messaging.queue.status`.

`messageQueueName` - имя очереди, из которой в ваш канал будут приходить сообщения (например: `my-channel-queue`).
Для каждого канала существует отдельная очередь и не должна пересекаться с очередью другого канала.

3. Для подключения необходимо добавить скрипт в БД со следующей строкой:
 ```roomsql
 INSERT INTO messaging.channel (id, name, queue_name, is_internal) 
        VALUES ('my_ch', 'My Channel', 'my-channel-queue', false)
 ```
 - id - уникальный код канала
 - name - имя канала, отображаемое на UI
 - queue_name - имя очереди канала (`my-channel-queue`)
 - is_internal - признак того, что канал является внутрисистемным 




