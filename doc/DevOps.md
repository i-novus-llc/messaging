# Пре-реквизиты
Для работы сервера нужен работающий инстанс apache kafka.
```shell
bin/zookeeper-server-start.sh config/zookeper.properties
bin/kafka-server-start.sh config/server.properties
```

# Запуск сервиса
1. Качаем `messaging-server-$VERSION.jar` c [сервера CI](https://ci.i-novus.ru/job/messaging/lastSuccessfulBuild/ru.i-novus.messaging$messaging-server/)
2. Создаём файл `application.yaml`
3. Указываем настройки соединения с БД
```yaml
spring.datasource:
  url: jdbc:postgresql://localhost/messaging
  username: postgres
  password: postgres
  driver-class-name: org.postgresql.Driver
```
4. Указываем настройки kafka
```yaml
spring.kafka.bootstrap-servers: localhost:9092
```
5. `java -jar messaging-server-$VERSION.jar`
6. Файл `application.yaml` должен быть в директории, откуда выполняется команда `java` или в поддиректории `config` 
