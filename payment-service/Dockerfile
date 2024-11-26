FROM maven:3.8.5-openjdk-17-slim AS build

WORKDIR /app

COPY pom.xml ./
COPY src ./src
RUN mvn clean package -DskipTests

COPY .env .env

FROM openjdk:17-jdk-alpine

WORKDIR /app

COPY target/payment-service-0.0.1-SNAPSHOT.jar ./payment-service-0.0.1-SNAPSHOT.jar

EXPOSE 8081

ENV DB_URL=mysql://database-users.mysql.database.azure.com:3306/users

ENTRYPOINT ["java", "-jar","-Dspring.datasource.url=${DB_URL}", "payment-service-0.0.1-SNAPSHOT.jar"]
