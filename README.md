Данный проект представляет собой реализацию сервиса для обмена сообщениями

Основные функции сервиса:
- оповещения пользователей
- обмен сообщениями между пользователями

# Требования
Для работы сервиса нужно, чтобы был запущен Apache Kafka или ActiveMQ.

# Структура проекта

- `messaging-n2o` -- standalone UI приложения для настройки уведомлений
- `messaging-n2o-conf` -- конфигурационные файлы N2O для встраивания в другие N2O приложения
- `messaging-rest` -- rest сервис уведомления + websocket сервер

# Документация
Подробнее см. [документацию](doc/Index.md)
