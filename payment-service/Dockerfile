FROM maven:3.8.5 AS build

WORKDIR /app

COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

FROM openjdk:17-jdk-alpine

WORKDIR /app

COPY target/payment-service-0.0.1-SNAPSHOT.jar ./app.jar
COPY payment.env ./payment.env

EXPOSE 8081

#ENV DB_URL=mysql://database-users.mysql.database.azure.com:3306/users

ENTRYPOINT ["java", "-jar","app.jar"]
