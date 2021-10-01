#Провайдеры
- security - SecurityAdminUserRoleProvider
  - Провайдер для получения данных из security-admin

- configurable - ConfigurableUserRoleProvider
  - Универсальный провайдер с возможностью указания маппинга полей из ответа сервиса предостовляющего информацию о пользователях и ролях, в модели для messaging.

#Настройки провайдеров
- `novus.messaging.user-role-provider`: configurable
  - Выбор провайдера данных о пользователе и ролях.
  - Доступные значения configurable или security.
- `novus.messaging.mapping-file-location`: classpath:userRoleProviderFieldMapping.xml
  -  Путь до файла для маппинга полей в ConfigurableUserRoleProvider
- `novus.messaging.user-provider-url`: http://localhost:9999/api/users
  - эдпоинт с которого провайдер будет забирать информацию о пользователях
- `novus.messaging.role-provider-url`: http://localhost:9999/api/role
  - эдпоинт с которого провайдер будет забирать  информацию о ролях(не нужен в случае SecurityAdminUserRoleProvider)

#Настройка маппинга в ConfigurableUserRoleProvider

```xml
<?xml version="1.0" encoding="UTF-8"?>
<mapping>
    <user content-mapping="content" count-mapping="totalElements">
        <response>
            <username mapping="username"/>
            <fio mapping="fio1"/>
            <email mapping="email"/>
            <surname mapping="surname"/>
            <name mapping="name"/>
            <patronymic mapping="patronymic"/>
            <roles mapping="roles1"
                   plain-string-array="true"
                   id-mapping="id1"
                   name-mapping="name1"
                   code-mapping="code1"
                   description-mapping="description1"/>
        </response>

        <criteria>
            <page-size mapping="size"/>
            <page-number mapping="page"/>
            <username mapping="username"/>
            <name mapping="name1"/>
            <fio mapping="fio1"/>
            <roleIds mapping="roleIds1"/>
        </criteria>
    </user>
    <role content-mapping="content" count-mapping="totalElements">
        <response>
            <id mapping="id1"/>
            <name mapping="name1"/>
            <code mapping="code1"/>
            <description mapping="description1"/>
        </response>

        <criteria>
            <page-size mapping="pageSize"/>
            <name mapping="name1"/>
            <page-number mapping="pageNumber"/>
            <permissionCodes mapping="permissionCodes1"/>
        </criteria>
    </role>
</mapping>
```

- `<user content-mapping="content" count-mapping="totalElements">`
  - content-mapping="content" путь до списка пользователей в мапе ответа. Через точку можно указать вложенный маппинг,
    например page.content.user
  - count-mapping="totalElements" маппинг количества сущностей подходящих под критерии запроса. Вложенный маппинг не
    поддерживается
  - данные атрибуты аналогично работают в теге \<role>

```xml
  <response>
    <username mapping="username"/>
    <fio mapping="fio1"/>
    <email mapping="email"/>
    <surname mapping="surname"/>
    <name mapping="name"/>
    <patronymic mapping="patronymic"/>
    <roles mapping="roles1"
           plain-string-array="true"
           id-mapping="id1"
           name-mapping="name1"
           code-mapping="code1"
           description-mapping="description1"/>
  </response>
```

- `<username mapping="username"/>` атрибут mapping различных тегов в <response> указывает название поля в ответе, которое нужно смапить в поле нашей модели, обусловленное названием тега
- Список всех доступных тегов приведен в примере в начале главы
- тег \<roles> особенный
  - mapping указывает на массив ролей
  - plain-string-array указание того, что это массив значений, а не объектов. 
  - plain-string-array нельзя использовать с атрибутами категории mapping из тега \<roles>
  - id/name/code/description-mapping указывают как мапить роли, если они представлены объектами.
  - если маппинг атрибуты ролей не указаны и plain-string-array=false или не указан, то будет взят маппинг из \<response> из \<role>
  

```xml
<criteria>
            <page-size mapping="size"/>
            <page-number mapping="page"/>
            <username mapping="username"/>
            <name mapping="name1"/>
            <fio mapping="fio1"/>
            <roleIds mapping="roleIds1"/>
        </criteria>
```
- Атрибут маппинг указывает как будет маппиться значение из нашей критерии в query-param