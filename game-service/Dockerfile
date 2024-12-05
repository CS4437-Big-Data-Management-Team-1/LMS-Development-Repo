FROM maven:3.8.5 AS build

WORKDIR /build

COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

FROM openjdk:17-jdk-alpine

WORKDIR /app

COPY target/game-service-0.0.1-SNAPSHOT.jar ./app.jar
COPY game.env ./game.env

EXPOSE 8083
#ENV DB_URL_GAMES=mysql://database-users.mysql.database.azure.com:3306/games

ENTRYPOINT ["java", "-jar","app.jar"]
