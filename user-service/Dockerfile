FROM maven:3.8.5 AS build

WORKDIR /app

COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

FROM openjdk:17-jdk-alpine

WORKDIR /app

COPY target/user-service-0.0.1-SNAPSHOT.jar ./app.jar
COPY user.env ./user.env

EXPOSE 8080

#ENV DB_URL=mysql://database-users.mysql.database.azure.com:3306/users "-Dspring.datasource.url=${DB_URL}"

ENTRYPOINT ["java", "-jar", "app.jar"]
