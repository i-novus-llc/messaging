Для того чтобы внедрить веб-интерфейс модуля уведомлений в существующее
приложение на N2O, нужно следующее:

1. Подключить библиотеку
```xml
<dependency>
   <group-id>ru.inovus.messaging</group-id>
   <artifact-id>messaging-n2o</artifact-id>
   <version>${messaging.version}</version>
</dependency>
```
2. Добавить spring bean класса `ru.inovus.messaging.n2o.UserMessageViewPageNameBinder`.
Он нужен, чтобы на странице просмотра сообщения можно было динамически строить название страницы.

В качестве примера в проект входит standalone веб админка для уведомлений (`messaging-admin-frontend`),
которая также включает ленту уведомлений и пользовательские настройки уведомлений.
