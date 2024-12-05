FROM maven:3.8.5 AS build

WORKDIR /app

COPY pom.xml ./
COPY src ./src
RUN mvn clean package -DskipTests


FROM openjdk:17-jdk-alpine

WORKDIR /app

COPY target/information-service-0.0.1-SNAPSHOT.jar ./app.jar
COPY information.env ./information.env

EXPOSE 8082
#ENV DB_URL_GAMES=mysql://database-users.mysql.database.azure.com:3306/games
#ENV DB_URL_TEAMS=mysql://database-users.mysql.database.azure.com:3306/teams

ENTRYPOINT ["java", "-jar","app.jar"]
