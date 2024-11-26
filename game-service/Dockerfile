FROM maven:3.8.5-openjdk-17-slim AS build

WORKDIR /build

COPY pom.xml ./
COPY src ./src
RUN mvn clean package -DskipTests

COPY .env .env


FROM openjdk:17-jdk-alpine

WORKDIR /app

COPY target/game-service-1.0-SNAPSHOT.jar ./game-service-1.0-SNAPSHOT.jar
COPY --from=build /build/.env .env

EXPOSE 8084
ENV DB_URL_GAMES=mysql://database-users.mysql.database.azure.com:3306/games


ENTRYPOINT ["java", "-jar","-Dspring.datasource.url=${DB_URL_GAMES}","game-service-0.0.1-SNAPSHOT.jar"]
