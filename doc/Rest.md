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
  "infoType": {
    "id": "notice",
    "name": "Центр уведомлений"
  },
  "recipientType": "USER",
  "systemId": "dev.1265"
  "recipients": [
    {
      "recipientType": "USER",
      "user": "1",
    }
  ]
}
```
Значения severity:
INFO - Информация,
WARNING - Предупреждение,
ERROR - Ошибка,
SEVERE - Важный;

Значения alertType:
BLOCKER - "Блокирующее сообщение";
POPUP - "Всплывающее сообщение";
HIDDEN - "Лента сообщений";

Значения recipientType:
USER - конкретным пользователям
ALL - всем


