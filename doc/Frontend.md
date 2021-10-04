Для того чтобы внедрить веб-интерфейс модуля уведомлений в существующее
приложение на N2O, нужно следующее:

Подключить библиотеку
```xml
<dependency>
   <group-id>ru.inovus.messaging</group-id>
   <artifact-id>messaging-admin-web</artifact-id>
   <version>${messaging.version}</version>
</dependency>
```
В качестве примера в проект входит standalone веб админка для уведомлений (`messaging-admin-frontend`),
которая также включает ленту уведомлений и пользовательские настройки уведомлений.
