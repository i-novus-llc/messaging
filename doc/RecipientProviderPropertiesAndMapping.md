# Свойства для настройки провайдера пользователей и конфигурация маппинга

## Провайдеры

- SecurityAdminRecipientProvider
    - для получения данных из security-admin

- ConfigurableRecipientProvider
    - конфигурируемый провайдер с возможностью указания маппинга полей из ответа сервиса, предоставляющего информацию о
      пользователях.

## Настройки провайдеров

- `novus.messaging.recipient-provider.type` - провайдер данных для получения пользователей и ролей. Доступные
  значения `configurable` или `security`(по умолчанию: `configurable`)
- `novus.messaging.recipient-provider.url` - адрес, с которого провайдер будет забирать информацию о получателях

## Настройки ConfigurableRecipientProvider

- `novus.messaging.recipient-provider.configurable.mapping-file-location` - путь до файла с маппингом полей
  для конфигурируемого провайдера (по умолчанию: `classpath:recipientProviderFieldMapping.xml`)


## Настройка маппинга в ConfigurableRecipientProvider

```xml
<?xml version="1.0" encoding="UTF-8"?>
<mapping>
    <recipient content-mapping="content" count-mapping="totalElements">
        <response>
            <username mapping="username"/>
            <fio mapping="fio1"/>
            <email mapping="email"/>
            <surname mapping="surname"/>
            <name mapping="name"/>
            <patronymic mapping="patronymic"/>
        </response>

        <criteria>
            <page-size mapping="size"/>
            <page-number mapping="page"/>
            <username mapping="username"/>
            <name mapping="name1"/>
            <fio mapping="fio1"/>
        </criteria>
    </recipient>
</mapping>
```

- `<user content-mapping="content" count-mapping="totalElements">`
    - content-mapping="content" путь до списка пользователей в мапе ответа. Через точку можно указать вложенный маппинг,
      например page.content.user
    - count-mapping="totalElements" маппинг количества сущностей подходящих под критерии запроса. Вложенный маппинг не
      поддерживается

```xml
<response>
    <username mapping="username"/>
    <fio mapping="fio1"/>
    <email mapping="email"/>
    <surname mapping="surname"/>
    <name mapping="name"/>
    <patronymic mapping="patronymic"/>
</response>
```

- `<username mapping="username"/>` атрибут mapping различных тегов в <response> указывает название поля в ответе,
  которое нужно смаппить в поле нашей модели, обусловленное названием тега
- Список всех доступных тегов приведен в примере в начале главы

```xml
<criteria>
    <page-size mapping="size"/>
    <page-number mapping="page"/>
    <username mapping="username"/>
    <name mapping="name1"/>
    <fio mapping="fio1"/>
</criteria>
```

- Атрибут маппинг указывает на то, как будет маппиться значение из критерия в query-param