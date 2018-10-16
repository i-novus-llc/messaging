# RESTful API
- Список сообщений
```
GET /messages?user=1
```
- Получение сообщения по идентификатору
```
GET /messages/123
```
- Отправка сообщений: 
```
POST /messages
```
```
{
  "caption": "Заголовок",
  "text": "Содержимое",
  "severity": "ERROR",
  "alertType": "BLOCKER",
  "sentAt": "2018-07-02T12:16:37",
  "recipients": [
    {
      "recipientType": "USER",
      "user": "18",
      "systemId": "regiona"
    }
  ]
}
```
