# Пре-реквизиты
Для работы сервера нужен работающий инстанс Apache Kafka или ActiveMQ.

## Запуск Kafka
```shell
bin/zookeeper-server-start.sh config/zookeper.properties
bin/kafka-server-start.sh config/server.properties
```

# Запуск сервиса
1. Качаем `messaging-server-$VERSION.jar` c [сервера CI](https://ci.i-novus.ru/job/messaging/lastSuccessfulBuild/ru.i-novus.messaging$messaging-server/)
2. Создаём файл `application.yaml`
3. Указываем настройки соединения с БД
```
spring.datasource:
  url: jdbc:postgresql://localhost/messaging
  username: postgres
  password: postgres
  driver-class-name: org.postgresql.Driver
```
4. Указываем какой профиль использовать: `kafka` - будет использоваться Kafka,
`activemq` - будет использоваться ActiveMQ. По умолчанию `kafka`. Чтобы использовать
ActiveMQ нужно задать профиль следующим образом
```
spring.profiles.active=activemq
```
4.1. Указываем настройки Kafka (если выбрали Kafka)
```
spring.kafka.bootstrap-servers: localhost:9092
```
4.2. Указываем настройки ActiveMQ (если выбрали ActiveMQ)
```
activemq.broker-url: tcp://192.168.1.1:61616
```
5. `java -jar messaging-server-$VERSION.jar`
6. Файл `application.yaml` должен быть в директории, откуда выполняется команда `java` или в поддиректории `config` 
