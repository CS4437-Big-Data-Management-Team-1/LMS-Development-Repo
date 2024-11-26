FROM maven:3.8.5-openjdk-17-slim AS build

WORKDIR /app

COPY pom.xml ./
COPY src ./src
RUN mvn clean package -DskipTests

COPY .env .env


FROM openjdk:17-jdk-alpine

WORKDIR /app

COPY target/information-service-0.0.1-SNAPSHOT.jar ./information-service-0.0.1-SNAPSHOT.jar


EXPOSE 8083
ENV DB_URL_GAMES=mysql://database-users.mysql.database.azure.com:3306/games
ENV DB_URL_TEAMS=mysql://database-users.mysql.database.azure.com:3306/teams

ENTRYPOINT ["java", "-jar","-Dspring.datasource.url=${DB_URL_GAMES}","-Dspring.datasource.url=${DB_URL_TEAMS}","information-service-0.0.1-SNAPSHOT.jar"]
