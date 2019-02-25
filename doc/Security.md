Аутентификация и авторизация
----------------------------

По умолчанию security выключен. Для включения нужно задать следующее
свойство в настройках приложения

```
security.enabled: true
```

# Настройки

Наиболее важные настройки security:

- `keycloak.server-url` -- путь к keycloak
- `keycloak.realm` -- realm keycloak
- `security.oauth2.resource.id` -- название клиента
- `security.oauth2.resource.jwt.key-value` -- публичный ключ для данного реалма
