# REST API сервиса уведомлений

- Список уведомлений
```
GET /messages?user=1
```
- Получение уведомлений по идентификатору
```
GET /messages/123
```
- Отправка уведомлений: 
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
BLOCKER - "Блокирующее уведомление";
POPUP - "Всплывающее уведомление";
HIDDEN - "Лента уведомлений";

Значения recipientType:
USER - конкретным пользователям
ALL - всем

