#Провайдеры
- Security - SecurityAdminUserRoleProvider
  - Провайдер для получения данных из security-admin

- Configurable - ConfigurableUserRoleProvider
  - Универсальный провайдер с возможностью указания маппинга полей из ответа сервиса, 
  предоставляющего информацию о пользователях и ролях.

#Настройки провайдеров
- `novus.messaging.user-role-provider` - провайдер данных для получения пользователей и ролей.
  Доступные значения `configurable` или `security`.
  (по умолчанию: `configurable`)
  
#Настройки ConfigurableUserRoleProvider
- `novus.messaging.mapping-file-location` - путь до файла для маппинга полей
  (по умолчанию: `classpath:userRoleProviderFieldMapping.xml`)
- `novus.messaging.user-provider-url` - эндпоинт, с которого провайдер будет забирать информацию о пользователях
  (по умолчанию: `http://localhost:9999/api/users`)
- `novus.messaging.role-provider-url` - эндпоинт, с которого провайдер будет забирать информацию о ролях
  (по умолчанию: `http://localhost:9999/api/role`)

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
  - content-mapping="content" путь до списка пользователей в мапе ответа. 
  Через точку можно указать вложенный маппинг, например page.content.user
  - count-mapping="totalElements" маппинг количества сущностей подходящих под критерии запроса. 
  Вложенный маппинг не поддерживается
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

- `<username mapping="username"/>` атрибут mapping различных тегов в <response> указывает название поля в ответе, 
которое нужно смаппить в поле нашей модели, обусловленное названием тега
- Список всех доступных тегов приведен в примере в начале главы
- тег \<roles> особенный
  - mapping указывает на массив ролей
  - plain-string-array указание того, что это массив значений, а не объектов. 
  - plain-string-array нельзя использовать с атрибутами категории mapping из тега \<roles>
  - id/name/code/description-mapping указывают как маппить роли, если они представлены объектами.
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
- Атрибут маппинг указывает на то, как будет маппиться значение из критерия в query-param