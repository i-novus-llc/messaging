Аутентификация и авторизация REST
=================================

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

# Передача токена в REST клиент и в вебсоккет канал

Если добавить аннотацию `@EnableOAuth2Sso` в `WebSecurityConfigurerAdapter`
(что выполняется автоматически при использовании `security-oauth2` из N2O), то spring
добавляет request scope bean класса `OAuth2UserContext`. Из него можно достать access token.
Его можно внедрить в вебсоккет канал через хедер `X-Auth-Token` (см. [описание websocket канала](Websocket.md).
Если `security.enabled=false`, то в `X-Auth-Token` надо передать `username` (см. `NoAuthAuthenticator`).

Для передачи токена в рест сервис через feign client, гуглить `feign client access token relay`.
