FROM openjdk:11-slim

EXPOSE 8080
COPY "target/messaging-admin-frontend.jar" app.jar
ENTRYPOINT ["java","-jar","app.jar"]