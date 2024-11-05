FROM maven:3.8.5-openjdk-17-slim AS build

WORKDIR /build

COPY pom.xml ./
RUN mvn dependency:go-offline

COPY src ./src
RUN mvn clean package -DskipTests


FROM openjdk:17-jdk-alpine
ARG PROFILE=dev
ARG APP_VERSION=0.0.1


WORKDIR /app

COPY --from=build /app/target/user-service-0.0.1-SNAPSHOT.jar ./user-service-0.0.1-SNAPSHOT.jar

EXPOSE 8080

ENV ACTIVE_PROFILE=${PROFILE}
ENV JAR_VERSION = ${APP_VERSION}

ENTRYPOINT ["java", "-jar","-Dspring.profiles.active=${ACTIVE_PROFILE}", "user-service-${JAR_VERSION}-SNAPSHOT.jar"]
