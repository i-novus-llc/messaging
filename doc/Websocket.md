Endpoint: `/ws`

## Формат сообщений

### Соединение
```json
{ "type" : "CONNECT", "headers" : { "X-Auth-Token" : "user-name-or-id"} }
```

Для авторизации пока что используется просто имя пользователя, в дальнейшем
это будет что-то типа JWT.

### Запрос количества не прочитанных сообещний
```json
{ "type" : "COUNT", "headers" : {"X-Auth-Token" : "user-id"} }
```

### Пометить сообщение прочитанным
```json
{ "type" : "READ", "message" : {"id" : "message-id" }, 
headers" : {"X-Auth-Token" : "user-id"} }
```

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