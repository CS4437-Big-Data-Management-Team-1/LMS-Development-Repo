FROM maven:3.8.5-openjdk-17-slim AS build

WORKDIR /build

COPY user.env ./

COPY pom.xml ./
RUN mvn dependency:go-offline

COPY src ./src
RUN mvn clean package -DskipTests


FROM openjdk:17-jdk-alpine
ARG PROFILE=dev
ARG APP_VERSION=0.0.1


WORKDIR /app

COPY target/user-service-0.0.1-SNAPSHOT.jar ./user-service-0.0.1-SNAPSHOT.jar

EXPOSE 8080

ENV DB_URL=mysql://database-users.mysql.database.azure.com:3306/users
ENV ACTIVE_PROFILE=${PROFILE}

ENTRYPOINT ["java", "-jar","-Dspring.profiles.active=${ACTIVE_PROFILE}","-Dspring.datasource.url=${DB_URL}", "user-service-0.0.1-SNAPSHOT.jar"]
