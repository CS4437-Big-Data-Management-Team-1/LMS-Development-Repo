FROM maven:3.8.5-openjdk-17-slim AS build

WORKDIR /build

COPY pom.xml ./
RUN mvn dependency:go-offline

COPY src ./src
RUN mvn clean package -DskipTests


FROM openjdk:17-jdk-alpine

WORKDIR /app

COPY --from=build /app/target/notification-service-0.0.1-SNAPSHOT.jar ./notification-service-0.0.1-SNAPSHOT.jar

EXPOSE 8081

ENTRYPOINT ["java", "-jar", "notification-service-0.0.1-SNAPSHOT.jar"]
