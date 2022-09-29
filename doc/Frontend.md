# Веб интерфейс сервиса уведомлений

Сервис содержит 3 модуля, отвечающих за отображение UI:

- `messaging-admin-frontend`
    - security
    - права доступа
    - профиль пользователя
- `messaging-admin-web`
    - уведомления
    - шаблоны уведомлений
- `messaging-web`
    - лента уведомлений


## Подключение
Для того чтобы внедрить веб-интерфейс уведомлений в существующее
приложение на N2O, достаточно подключить модуль `messaging-admin-web`.
```xml
<dependency>
   <group-id>ru.inovus.messaging</group-id>
   <artifact-id>messaging-admin-web</artifact-id>
   <version>${messaging.version}</version>
</dependency>
```
Этот модуль содержит UI для работы с уведомлениями и с шаблонами уведомлений.
Также он включает модуль `messaging-web`, который позволяет подключать ленту уведомлений.


## Фронтенд приложение
В дополнении, в проект входит запускаемый модуль `messaging-admin-frontend`,
содержащий все выше указанные модули UI.

## Перенаправление запросов на загрузку/скачивание/удаление вложений
При включенной возможности прикрепления вложений к уведомлениям `novus.messaging.attachment.enabled=true` на UI появятся поля для загрузки и скачивания вложений. Необходимо настроить перенапраление запросов на бэкенд сервис. Запрос на `адрес_frontend_модуля/proxy/api/attachments` необходимо перенаправить на `адрес_backend_модуля/api/attachments` прокси-клиентом.

Пример настройки прокси-клиента:

```xml
<dependency>
  <groupId>org.mitre.dsmiley.httpproxy</groupId>
  <artifactId>smiley-http-proxy-servlet</artifactId>
  <version>${proxy.version}</version>
</dependency>
```

```java
    @Bean
    public ServletRegistrationBean<ProxyServlet> proxyInputDataServiceServlet(
            @Value("${messaging.backend.path}" + "/attachments") String inputDataUrl) {
        ServletRegistrationBean<ProxyServlet> bean =
                new ServletRegistrationBean<>(new ProxyServlet(), "/proxy/api/attachments/*");
        Map<String, String> params = new HashMap<>();
        params.put("targetUri", inputDataUrl);
        params.put(ProxyServlet.P_LOG, "true");
        params.put(ProxyServlet.P_PRESERVECOOKIES, "true");
        bean.setInitParameters(params);
        return bean;
    }
```