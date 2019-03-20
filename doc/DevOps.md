# Пре-реквизиты
Для работы сервера нужен работающий инстанс Apache Kafka или ActiveMQ.
## Сборка сервера
Сервер работает в 3х режимах
1. Встроенный брокер ActiveMQ ( профили maven: `-P embedded-broker`) включен по умолчанию
2. Выделенный брокер ActiveMQ ( профили maven: `-P activemq` )
3. Kafka ( профили maven: `-P kafka` )

## Запуск Kafka
```shell
bin/zookeeper-server-start.sh config/zookeper.properties
bin/kafka-server-start.sh config/server.properties
```

# Запуск сервиса
1. Качаем `messaging-server-$VERSION.jar` c сервера CI [сборку с поддержкой ActiveMQ](https://ci.i-novus.ru/job/messaging/lastSuccessfulBuild/ru.i-novus.messaging$messaging-server/)
 или [сборку с поддержкой Kafka](https://ci.i-novus.ru/job/messaging.kafka/lastSuccessfulBuild/ru.i-novus.messaging$messaging-server/)
2. Создаём файл `application.yaml`
3. Указываем настройки соединения с БД
```
spring.datasource:
  url: jdbc:postgresql://localhost/messaging
  username: postgres
  password: postgres
  driver-class-name: org.postgresql.Driver
```
4.1. Указываем настройки Kafka (если выбрали Kafka)
```
spring.kafka.bootstrap-servers: localhost:9092
```
4.2. Указываем настройки ActiveMQ (если выбрали ActiveMQ)
```
spring.activemq.broker-url: tcp://192.168.1.1:61616
```
5. `java -jar messaging-server-$VERSION.jar`
6. Файл `application.yaml` должен быть в директории, откуда выполняется команда `java` или в поддиректории `config` 

# Запуск админки уведомлений
 Необходимо выполнить комнады npm для messaging-frontend/messaging-react

```
npm i --registry https://npm.i-novus.ru
npm run build
```

Затем собрать maven messaging-frontend профиль production 