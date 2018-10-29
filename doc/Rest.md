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
"message": {
  "caption": "Test caption",
  "text": "Test content",
      "severity": "INFO",
      "alertType": "POPUP",
      "sentAt": "2018-10-16T07:38:13.07"},
	"recipients": [
    {
      "recipientType": "USER",
      "user": "1",
      "systemId": "dev.1265"
    }
  ]
}
```
