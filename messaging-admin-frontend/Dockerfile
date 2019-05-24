FROM openjdk:11-slim

EXPOSE 8080

RUN apt-get update && apt-get install -y curl

COPY "target/messaging-admin-frontend.jar" app.jar
ENTRYPOINT ["java","-jar","app.jar"]