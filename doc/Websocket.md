Endpoint: `/ws`

## Формат сообщений

### Соединение
```json
{ "type" : "CONNECT", "headers" : { "X-Auth-Token" : "user-name-or-access-token"
                                  , "X-System-Id" : "system-id" } }
```

Хедер `X-System-Id` нужен для того, чтобы можно было использовать один и тот же сервис
сообщений с разными системами. В МТР это разные региональные системы 
и центральный. Все хедеры являются обязательными. Без них сервис не сможет
идентифицировать клиента.

Описание `X-Auth-Token` см. в разделе [Security](Security.md).

### Запрос количества не прочтенных сообещний
```json
{ "type" : "COUNT", "headers" : { "X-System-Id" : "system-id" } }
```

### Подтверждение прочтения
```json
{ "type" : "READ", "headers" : { "X-System-Id" : "system-id" }
, "message" : { "id" : "message-id" }}
```
Если не указать `message.id`, то *все* сообщения для данного пользователя будут
помечены прочтенными.

## Сообщения

Сообщения, которые приходят с сервера, будут иметь следующий вид:


### Уведомления 
```json
{ "message" : { "id" : 123
              , "subject" : "Тема"
              , "content" : "Содержимое"
              , "contentType" : "PLAIN/HTML/MARKDOWN"
              , "severity" : "INFO/ERROR/WARNING/SEVERE"
              , "alertType" : "BLOCKER/POPUP/HIDDEN"}
, "recipients" : [ { "recipientType" : "USER", "user" : "foo"}
                 , { "recipientType" : "ALL" } ]
}
```
### Количество не прочтенных уведомлений
```json
{ "count" : 3 }
```