FROM harbor.i-novus.ru/library/redos7c/jdk-17:17.0.10-1.30.0

EXPOSE 8080

ARG JAR_FILE
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java","-jar","app.jar","--server.port=8080"]
