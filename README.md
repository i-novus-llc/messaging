Данный проект представляет собой реализацию сервиса для обмена сообщениями

Основные функции сервиса:
- оповещения пользователей
- обмен сообщениями между пользователями

# Требования
Для работы сервиса нужно, чтобы был запущен Apache Kafka или ActiveMQ.

# Запуск сервера

1. Добавить файл `config/application.yaml` или `config/application.properties`.
2. Переопределить необходимые [свойства](doc/Properties.md).
3. Выполнить `mvnw install spring-boot:run -pl messaging-server`.

# Использование

Подключаем библиотеку
```xml
<dependency>
    <groupId>ru.inovus.messaging</groupId>
    <artifactId>messaging-n2o-plugin</artifactId>
    <version>1.0-SNAPSHOT</version>
</dependency>
```
В свойствах приложения указываем
```properties
messaging.system.ws.url=путь к точке доступа по каналу web sockets
messaging.system.rest.url=путь к точке доступа REST сервиса
```

# Админка
[http://localhost:8080/admin.html](http://localhost:8080/admin.html)

# Документация
Подробнее см. [документацию](doc/Index.md)
